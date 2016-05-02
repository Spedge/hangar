package com.spedge.hangar.repo.java;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.index.IndexKey;

public class JavaDownloadRepository extends JavaRepository {

	
	@GET
	@Path("/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+-SNAPSHOT}/{filename : [^/]+}")
	public StreamingOutput getArtifact(@PathParam("group") String group, 
			                           @PathParam("version") String version,
			                           @PathParam("artifact") String artifact,
			                           @PathParam("filename") String filename)
	{
		IndexKey key = new JavaIndexKey(group, artifact, version);
	    logger.info("Downloading " + key);
	    
		if(getIndex().isArtifact(key))
		{
			return getStorage().getArtifactStream(getIndex().getArtifact(key), filename);
		}
		else
		{
			throw new NotFoundException();
		}
	}
}
