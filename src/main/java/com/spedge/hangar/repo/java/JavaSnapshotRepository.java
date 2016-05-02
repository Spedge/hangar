package com.spedge.hangar.repo.java;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.storage.StorageException;

public class JavaSnapshotRepository extends JavaRepository
{
	@GET
	@Path("/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+-SNAPSHOT}/{filename : [^/]+}")
	public StreamingOutput getArtifact(@PathParam("group") String group, 
			 						   @PathParam("artifact") String artifact,
			                           @PathParam("version") String version,
			                           @PathParam("filename") String filename)
	{
		IndexKey key = new JavaIndexKey(group.replace('/', '.'), artifact, version);
	    logger.debug("[Downloading Snapshot] " + key);
	    
		if(getIndex().isArtifact(key))
		{
			return getStorage().getArtifactStream(getIndex().getArtifact(key), filename);
		}
		else
		{
			throw new NotFoundException();
		}
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+-SNAPSHOT}/{filename : [^/]+}")
	public Response uploadArtifact(@PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
					               @PathParam("version") String version,
					               @PathParam("filename") String filename,
			                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(group.replace('/', '.'), artifact, version);
		key = (getIndex().isArtifact(key))? new JavaIndexKey(group.replace('/', '.'), artifact) : key;
		logger.debug("[Uploading Snapshot] " + key.toString());
		
		IndexArtifact ia = getStorage().generateArtifactPath(key);
		getIndex().addArtifact(key, ia);
		try 
		{
			getStorage().uploadSnapshotArtifactStream(ia, filename, uploadedInputStream);
			return Response.ok().build();
		} 
		catch (StorageException e) 
		{
			throw new InternalServerErrorException();
		}
		
	}
	
	@GET
	@Path("/{dummy2 : (snapshots)?}{dummy3 : (/)?}{group : .+}{dummy4 : (/)?}{artifact : .+}{dummy5 : (/)?}maven-metadata.xml{type : (\\.)?(\\w)*}")
	public StreamingOutput getMetadata(@PathParam("group") String group, 
			 						   @PathParam("artifact") String artifact,
						               @PathParam("version") String version)
	{
		IndexKey key = new JavaIndexKey(group.replace('/', '.'), artifact, version);
		key = (getIndex().isArtifact(key))? new JavaIndexKey(group.replace('/', '.'), artifact) : key;
	    logger.debug("[Downloading Metadata] " + key);
	    
		if(getIndex().isArtifact(key))
		{
			return getStorage().getArtifactStream(getIndex().getArtifact(key), "maven-metadata.xml");
		}
		else
		{
			throw new NotFoundException();
		}
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/{dummy2 : (snapshots)?}{dummy3 : (/)?}{group : .+}{dummy4 : (/)?}{artifact : .+}{dummy5 : (/)?}maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadMetadata(@PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
			                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(group.replace('/', '.'), artifact);
		key = (getIndex().isArtifact(key))? new JavaIndexKey(group.replace('/', '.'), artifact) : key;
		logger.debug("[Uploading Metadata] " + key.toString());
		
		IndexArtifact ia = getStorage().generateArtifactPath(key);
		getIndex().addArtifact(key, ia);
		
		try 
		{
			getStorage().uploadSnapshotArtifactStream(ia, "maven-metadata", uploadedInputStream);
			return Response.ok().build();
		} 
		catch (StorageException e) 
		{
			throw new InternalServerErrorException();
		}
	}
	

}
