package com.spedge.hangar.repo.java.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;

public class JavaReleaseAPI extends JavaRepository
{
	private RepositoryType repositoryType = RepositoryType.RELEASE_JAVA;
		
	@GET
	@Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+}/{filename : [^/]+}")
	public StreamingOutput getArtifact(@PathParam("group") String group, 
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
		
		try 
		{
			if(getIndex().isArtifact(key))
			{
				if(getIndex().getArtifact(key).isStoredFile(filename))
			    {
			    	throw new WebApplicationException(Status.CONFLICT);
			    }
			}
		} 
		catch (IndexException e) 
		{
			throw new InternalServerErrorException();
		}
	    	
		return addArtifact(key, filename, uploadedInputStream);
	}
	
	@GET
	@Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]*}/maven-metadata.xml{type : (\\.)?(\\w)*}")
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
	@Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]*}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadMetadata(@PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
								   @PathParam("type") String type,
			                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.') + ":" + artifact);
		logger.debug("[Uploading Release] " + key.toString());
		
		if(type.isEmpty())
		{
			return addArtifact(key, "maven-metadata.xml" + type, uploadedInputStream);
		}
		else
		{
			return addMetadata(key, uploadedInputStream);
		}
	}
	
	/*
	 * This allows us to download the top level metadata - which won't have a version.
	 * Example Path : /releases/com/spedge/hangar-artifact/maven-metadata.xml
	 */
	@GET
	@Path("/releases/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public StreamingOutput getToplevelMetadata(@PathParam("group") String group, 
					 						   @PathParam("artifact") String artifact,
											   @PathParam("type") String type)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, "metadata");
	    logger.debug("[Downloading Metadata] " + key.toString());
	    
		return getArtifact(key, "maven-metadata.xml" + type);
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/releases/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadTopLevelMetadata(@PathParam("group") String group, 
										   @PathParam("artifact") String artifact,
										   @PathParam("type") String type,
					                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, "metadata");
		logger.debug("[Uploading Metadata] " + key.toString());
		
		if(! type.isEmpty())
		{
			return addArtifact(key, "maven-metadata.xml" + type, uploadedInputStream);
		}
		else
		{
			return addMetadata(key, uploadedInputStream);
		}
	}
	
	public RepositoryType getType()
	{
		return repositoryType;
	}
}
