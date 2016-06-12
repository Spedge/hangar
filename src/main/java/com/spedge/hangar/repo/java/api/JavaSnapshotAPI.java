package com.spedge.hangar.repo.java.api;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaSnapshotRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;

public class JavaSnapshotAPI extends JavaSnapshotRepository
{
	private RepositoryType repositoryType = RepositoryType.SNAPSHOT_JAVA;
	
	@GET
	@Path("/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+-SNAPSHOT}/{filename : [^/]+}")
	public StreamingOutput getSnapshotArtifact(@PathParam("group") String group, 
			 						           @PathParam("artifact") String artifact,
			                                   @PathParam("version") String version,
			                                   @PathParam("filename") String filename)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
	    logger.debug("[Downloading Snapshot] " + key);
	    
		return getSnapshotArtifact(key, filename);
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
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
		logger.debug("[Uploading Snapshot] " + key.toString());
		
		return addArtifact(key, filename, uploadedInputStream);
	}
	
	/*
	 * This allows us to download the top level metadata - which won't have a version.
	 * Example Path : /snapshots/com/spedge/hangar-artifact/maven-metadata.xml
	 */
	@GET
	@Path("/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public StreamingOutput getToplevelMetadata(@PathParam("group") String group, 
					 						   @PathParam("artifact") String artifact,
											   @PathParam("type") String type)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.') + ":" + artifact);
	    logger.debug("[Downloading Metadata] " + key.toString());
	    
		return getArtifact(key, "maven-metadata.xml" + type);
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadTopLevelMetadata(@PathParam("group") String group, 
										   @PathParam("artifact") String artifact,
										   @PathParam("type") String type,
					                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.') + ":" + artifact);
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

	/*
	 * This allows us to download the version metadata.
	 * Example Path : /snapshots/com/spedge/hangar-artifact/0.0.2-SNAPSHOT/maven-metadata.xml
	 */
	@GET
	@Path("/snapshots/{group : .+}/{artifact : .+}/{version : ([\\d\\.]*\\-SNAPSHOT)+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public StreamingOutput getMetadata(@PathParam("group") String group, 
			 						   @PathParam("artifact") String artifact,
						               @PathParam("version") String version,
									   @PathParam("type") String type)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
	    logger.debug("[Downloading Metadata] " + key.toString());
	    
		return getArtifact(key, "maven-metadata.xml" + type);
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/snapshots/{group : .+}/{artifact : .+}/{version : ([\\d\\.]*\\-SNAPSHOT)+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadMetadata(@PathParam("group") String group, 
									@PathParam("artifact") String artifact,
									@PathParam("version") String version,
									@PathParam("type") String type,
					                InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
		logger.debug("[Uploading Metadata] " + key.toString());
		
		if(! type.isEmpty())
		{
			return addArtifact(key, "maven-metadata.xml" + type, uploadedInputStream);
		}
		else
		{
			return addSnapshotMetadata(key, uploadedInputStream);
		}
	}
	
	
	public RepositoryType getType()
	{
		return repositoryType;
	}
}
