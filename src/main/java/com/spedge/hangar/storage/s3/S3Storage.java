package com.spedge.hangar.storage.s3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaIndexArtifact;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.Storage;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.local.LocalStorageException;

public class S3Storage extends Storage
{
	private AmazonS3 client;
	private S3StorageHealthcheck check;
	
	@NotNull
	@JsonProperty
	private String bucketName;
	
	@JsonProperty
	private String serialNumber;
	
	@JsonProperty
	private String region = "eu-west-1";
	private String mfaToken = System.getenv("MFA_TOKEN");
	
	@Override
	public HealthCheck getHealthcheck() {
		if(check == null) { check = new S3StorageHealthcheck(getPath()); }
		return check;
	}

	@Override
	public void initialiseStorage(String uploadPath) throws StorageException 
	{	
		// Unlike LocalStorage that requires paths, S3 works off of keys (like Zookeeper) so we don't
		// actually need to initalise the path. However, we'll use the opportunity to create the client
		// and authenticate via MFA if we need to.
		if(client == null)
		{
			AWSCredentials creds = new EnvironmentVariableCredentialsProvider().getCredentials();
			
			// If we're running this locally, we may need to start this up with our MFA token (I know...)
			if(serialNumber != null)
			{
				AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient();
				
				// Manually start a session.
				GetSessionTokenRequest getSessionTokenRequest = new GetSessionTokenRequest();
				
				getSessionTokenRequest.setSerialNumber(serialNumber);
				getSessionTokenRequest.setTokenCode(mfaToken);
				getSessionTokenRequest.setDurationSeconds(7200); 
				
				// Following duration can be set only if temporary credentials are requested by an IAM user.
				GetSessionTokenResult sessionTokenResult = stsClient.getSessionToken(getSessionTokenRequest);
				Credentials sessionCredentials = sessionTokenResult.getCredentials();
				  
				// Package the temporary security credentials as 
				// a BasicSessionCredentials object, for an Amazon S3 client object to use.
				creds = new BasicSessionCredentials(sessionCredentials.getAccessKeyId(), 
				        		                    sessionCredentials.getSecretAccessKey(), 
				        		                    sessionCredentials.getSessionToken());
			}
			
			// Now we create our client.
			client = new AmazonS3Client(creds);
			client.setEndpoint("s3-" + region + ".amazonaws.com");
		}
	}
	
	@Override
	public List<IndexKey> getArtifactKeys(RepositoryType type, String uploadPath) throws StorageException 
	{
		List<IndexKey> indices = new ArrayList<IndexKey>();
		
		for(S3ObjectSummary summary : client.listObjects(bucketName, getPath() + "/" + uploadPath).getObjectSummaries())
		{
			String key = summary.getKey();
			if(key.endsWith("/"))
			{
				indices.add(new JavaIndexKey(type, key.substring(0, key.length() - 1)));
			}
		}
		
		return indices;
	}

	@Override
	public StreamingOutput getArtifactStream(IndexArtifact artifact, String filename) 
	{
		final String artifactPath = getPath() + artifact.getLocation() + "/" + filename;
		
		if(client.doesObjectExist(bucketName, artifactPath))
    	{
			return new StreamingOutput() {
	            
	            public void write(OutputStream os) throws IOException, WebApplicationException 
	            {
	            	GetObjectRequest gor = new GetObjectRequest(bucketName, artifactPath);
	        		S3Object so = client.getObject(gor);
	        		ByteStreams.copy(so.getObjectContent(), os);
	        		so.close();
	            }
	        };
    	}
		else
		{
			throw new NotFoundException();
		}
	}

	@Override
	public void uploadReleaseArtifactStream(IndexArtifact key, String filename, InputStream uploadedInputStream) throws StorageException 
	{
		uploadArtifactStream(key, filename, uploadedInputStream);		
	}

	@Override
	public void uploadSnapshotArtifactStream(IndexArtifact key, String filename, InputStream uploadedInputStream) throws StorageException 
	{
		uploadArtifactStream(key, filename, uploadedInputStream);		
	}
		
	private void uploadArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream) throws LocalStorageException 
	{
		try 
		{
			TransferManager tx = new TransferManager(client);
			ObjectMetadata om = new ObjectMetadata();
			om.setContentLength(uploadedInputStream.available());
						
			Upload myUpload = tx.upload(bucketName, getPath() + artifact.getLocation() + "/" + filename, uploadedInputStream, om); 
			myUpload.waitForCompletion();
		} 
		catch (Exception e) 
		{
			logger.error(e.getLocalizedMessage());
			throw new LocalStorageException();
		}
	}

	@Override
	protected IndexArtifact generateJavaArtifactPath(JavaIndexKey key, String uploadPath) throws LocalStorageException 
	{
		IndexArtifact ia = new JavaIndexArtifact();
		String version = key.getVersion().isEmpty()? "" : "/" + key.getVersion();
		String location = "/" + uploadPath + "/" + key.getGroup().replace('.', '/') + "/" + key.getArtifact() + version;
		ia.setLocation(location);
		
		return ia;
	}
}
