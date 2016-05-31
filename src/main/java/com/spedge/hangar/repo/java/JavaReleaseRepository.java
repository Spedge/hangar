package com.spedge.hangar.repo.java;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;

public class JavaReleaseRepository extends JavaRepository
{
	private RepositoryType repositoryType = RepositoryType.RELEASE_JAVA;
		
	@GET
	@Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+}/{filename : [^/]+}")
	public StreamingOutput getSnapshotArtifact(@PathParam("group") String group, 
			 						           @PathParam("artifact") String artifact,
			                                   @PathParam("version") String version,
			                                   @PathParam("filename") String filename)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
	    logger.debug("[Downloading Release] " + key);
	    
	    return getArtifact(key, filename);
	}
		
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+}/{filename : [^/]+}")
	public Response uploadArtifact(@PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
					               @PathParam("version") String version,
					               @PathParam("filename") String filename,
			                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
		logger.debug("[Uploading Release] " + key.toString());
		
		if(getIndex().isArtifact(key))
		{
			if(getIndex().getArtifact(key).isStoredFile(filename))
		    {
		    	throw new WebApplicationException(Status.CONFLICT);
		    }
		}
	    	
		return addArtifact(key, filename, uploadedInputStream);
	}
	
	@GET
	@Path("/{dummy2 : (releases)?}{dummy3 : (/)+}{group : .+}{dummy4 : (/)+}{artifact : .+}{dummy5 : (/)?}{version : (?i)[\\d\\.]*}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public StreamingOutput getMetadata(@PathParam("group") String group, 
			 						   @PathParam("artifact") String artifact,
						               @PathParam("version") String version,
									   @PathParam("type") String type)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
	    logger.debug("[Downloading Release] " + key.toString());
	    
		return getArtifact(key, "maven-metadata.xml" + type);
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/{dummy2 : (releases)?}{dummy3 : (/)+}{group : .+}{dummy4 : (/)+}{artifact : .+}{dummy5 : (/)?}{version : (?i)[\\d\\.]*}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadMetadata(@PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
								   @PathParam("type") String type,
			                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.') + ":" + artifact);
		logger.debug("[Uploading Release] " + key.toString());
    	return addArtifact(key, "maven-metadata.xml" + type, uploadedInputStream);
	}
	
	public RepositoryType getType()
	{
		return repositoryType;
	}
}
