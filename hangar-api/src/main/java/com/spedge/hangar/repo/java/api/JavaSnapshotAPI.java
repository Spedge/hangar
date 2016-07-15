package com.spedge.hangar.repo.java.api;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaSnapshotRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageRequest;

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
	public Response uploadArtifact(@Context final HttpServletRequest request,
			                       @PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
					               @PathParam("version") String version,
					               @PathParam("filename") String filename,
			                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
		logger.debug("[Uploading Snapshot] " + key.toString());
		
		StorageRequest sr = new StorageRequest();
		sr.setFilename(filename);
		sr.setLength(request.getContentLength());
		sr.setStream(uploadedInputStream);
		
		return addArtifact(key, sr);
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
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, "metadata");
	    logger.debug("[Downloading Metadata] " + key.toString());
	    
		return getArtifact(key, "maven-metadata.xml" + type);
	}
	
	@PUT
	@Consumes(MediaType.WILDCARD)
	@Path("/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
	public Response uploadTopLevelMetadata(@Context final HttpServletRequest request,
			                               @PathParam("group") String group, 
										   @PathParam("artifact") String artifact,
										   @PathParam("type") String type,
					                       InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, "metadata");
		logger.debug("[Uploading Metadata] " + key.toString());
		
		StorageRequest sr = new StorageRequest();
		sr.setLength(request.getContentLength());
		sr.setStream(uploadedInputStream);
		
		if(! type.isEmpty())
		{
			sr.setFilename("maven-metadata.xml" + type);
			return addArtifact(key, sr);
		}
		else
		{
			sr.setFilename("maven-metadata.xml");
			return addMetadata(key, sr);
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
	public Response uploadMetadata(@Context final HttpServletRequest request,
			                       @PathParam("group") String group, 
								   @PathParam("artifact") String artifact,
								   @PathParam("version") String version,
								   @PathParam("type") String type,
					               InputStream uploadedInputStream)
	{
		JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
		logger.debug("[Uploading Metadata] " + key.toString());
		
		StorageRequest sr = new StorageRequest();
		sr.setLength(request.getContentLength());
		sr.setStream(uploadedInputStream);
		
		if(! type.isEmpty())
		{
			sr.setFilename("maven-metadata.xml" + type);
			return addArtifact(key, sr);
		}
		else
		{
			sr.setFilename("maven-metadata.xml");
			return addSnapshotMetadata(key, sr);
		}
	}
	
	
	public RepositoryType getType()
	{
		return repositoryType;
	}
}
