package com.spedge.hangar.repo.java;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

public class JavaDownloadRepository extends JavaRepository {

	
	@GET
	@Path("/{group : .+}/{artifact : .+}/{version : .+}/{filename : [^/]+}")
	public StreamingOutput getArtifact(@PathParam("group") String group, 
			                           @PathParam("version") String version,
			                           @PathParam("artifact") String artifact,
			                           @PathParam("filename") String filename)
	{
		JavaIndexKey key = new JavaIndexKey(group.replace('/', '.'), artifact, version);
	    logger.info("[Downloading Artifact] " + key);
	    
		return getArtifact(key, filename);
	}
}
