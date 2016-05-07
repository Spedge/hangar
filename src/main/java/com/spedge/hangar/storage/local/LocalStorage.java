package com.spedge.hangar.storage.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.java.JavaIndexKey;
import com.spedge.hangar.storage.Storage;

public class LocalStorage extends Storage
{
	private HealthCheck check = null;
	
	@NotEmpty
	private String path;
	
	@NotEmpty
	private String size;

	public HealthCheck getHealthcheck() 
	{
		if(check == null) { check = new LocalStorageHealthcheck(path, size); }
		return check;
	}

	@JsonProperty
	public String getPath() {
		return path;
	}

	@JsonProperty
	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty
	public String getSize() {
		return size;
	}

	@JsonProperty
	public void setSize(String size) {
		this.size = size;
	}
	
	// From the JavaIndexKey (containing GAV params)
	// generate the path that it should go into on local storage.
	@Override
	protected IndexArtifact generateJavaArtifactPath(JavaIndexKey key)
	{
		IndexArtifact ia = new IndexArtifact();
		String version = key.getVersion().isEmpty()? "" : "/" + key.getVersion();
		ia.setLocation("/" + key.getGroup().replace('.', '/') + "/" + key.getArtifact() + version);
		return ia;
	}
	
	@Override
	public List<IndexKey> getArtifactKeys() throws LocalStorageException
	{
		try 
		{
			
			List<IndexKey> paths = Files.walk(Paths.get(path))
								        .filter(Files::isRegularFile)
								        .map(e -> e.toString().replace(path, ""))
								        .map(e -> e.subSequence(0, e.lastIndexOf("\\")).toString())
								        .map(e -> e.substring(1, StringUtils.lastOrdinalIndexOf(e, "\\", 2)).replace("\\", ".") 
								        		                   + ":" + e.substring(StringUtils.lastOrdinalIndexOf(e, "\\", 2), e.lastIndexOf("\\")).replace("\\", "")
								        		                   + ":" + e.substring(e.lastIndexOf("\\"), e.length()).replace("\\", ""))
								        .distinct()
								        .map(e -> new IndexKey(type, e))
								        .collect(Collectors.toList());
			
			logger.info(paths.size() + " Artifacts Indexed under " + path);
			return paths;
		} 
		catch (IOException e) 
		{
			logger.info(e.getMessage());
			throw new LocalStorageException();
		}
	}

	@Override
	public StreamingOutput getArtifactStream(final IndexArtifact artifact, final String filename) {
		
		final String artifact_path = path + artifact.getLocation() + "/" + filename;
		
		if(Files.isReadable(Paths.get(artifact_path)))
    	{
			return new StreamingOutput() {
	            
	            public void write(OutputStream os) throws IOException, WebApplicationException 
	            {
            		ByteStreams.copy(new FileInputStream(artifact_path), os);
	            }
	        };
    	}
		else
		{
			throw new NotFoundException();
		}
	}
	
	public void uploadReleaseArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream) throws LocalStorageException {
		uploadArtifactStream(artifact, filename, uploadedInputStream);
	}

	public void uploadSnapshotArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream) throws LocalStorageException {
		uploadArtifactStream(artifact, filename, uploadedInputStream, StandardCopyOption.REPLACE_EXISTING);
	}

	private void uploadArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream, StandardCopyOption... options) throws LocalStorageException {
		
		Path outputPath = FileSystems.getDefault().getPath(path + artifact.getLocation());
		Path outputPathArtifact = FileSystems.getDefault().getPath(path + artifact.getLocation() + "/" + filename);
		
		try 
		{
			Files.createDirectories(outputPath);
			Files.copy(uploadedInputStream, outputPathArtifact, options);
			uploadedInputStream.close();
		} 
		catch (IOException e) 
		{
			logger.error(e.getLocalizedMessage());
			throw new LocalStorageException();
		}
	}	
}
