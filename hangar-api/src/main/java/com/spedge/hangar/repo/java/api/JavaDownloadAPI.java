package com.spedge.hangar.repo.java.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.input.TeeInputStream;
import org.eclipse.jetty.http.HttpStatus;

import com.google.common.io.ByteStreams;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageException;

public class JavaDownloadAPI extends JavaRepository {

	private RepositoryType repositoryType = RepositoryType.PROXY_JAVA;
	private String[] proxy;
	
	@GET
	@Path("/{group : .+}/{artifact : .+}/{version : .+}/{filename : [^/]+}")
	public StreamingOutput getArtifact(@PathParam("group") String group, 
			                           @PathParam("version") String version,
			                           @PathParam("artifact") String artifact,
			                           @PathParam("filename") String filename)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
		logger.info("[Downloading Artifact] " + key);
	    
	    try
	    {
	      	return getArtifact(key, filename);
	    }
	    catch(NotFoundException nfe)
	    {
	    	try
	    	{    			
		    	for(String source : proxy)
		    	{
			    	// So the artifact doesn't exist. We try and download it and save it to disk.
			    	Client client = ClientBuilder.newClient();
			    	WebTarget target = client.target(source).path(group + "/" + artifact + "/" + version + "/" + filename);
			    	
			    	Invocation.Builder builder = target.request(MediaType.WILDCARD);
			    	Response resp = builder.get();
	
			    	if(resp.getStatus() == HttpStatus.OK_200)
			    	{
			    		logger.info("[Proxy] Downloading from " + source);
			    		
			    		IndexArtifact ia = getStorage().generateArtifactPath(getType(), getPath(), key);
			    		
			    		// Let's download the item from the proxy and close the connection.
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						InputStream in = new TeeInputStream(resp.readEntity(InputStream.class), out);
						
						// Now upload the artifact to our proxy location.
						getStorage().uploadSnapshotArtifactStream(ia, filename, in);
						resp.close();
						
						// We should add it to the index now. Most of these don't have metadata, so we
						// add it to the index as soon as we get it.
						getIndex().addArtifact(key, ia);
						
						// We take our copy and return it to the user post-upload.
						final InputStream writer = new ByteArrayInputStream(out.toByteArray());
						return new StreamingOutput() {
				            
				            public void write(OutputStream os) throws IOException, WebApplicationException 
				            {
			            		ByteStreams.copy(writer, os);
			            		writer.close();
				            }
				        };
			    	}
		    	}
					
		    	throw new NotFoundException();	    		
	    	} 
	    	catch (StorageException | IndexConfictException | IndexException e) 
	    	{
				throw new InternalServerErrorException();
			}
	    }
	}
	
	public String[] getProxy() {
		return proxy;
	}

	public void setProxy(String[] proxy) {
		this.proxy = proxy;
	}

	@Override
	public RepositoryType getType() {
		return repositoryType;
	}
}
