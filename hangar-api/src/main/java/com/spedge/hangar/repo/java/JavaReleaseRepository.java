package com.spedge.hangar.repo.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;

import org.apache.commons.io.input.TeeInputStream;
import org.eclipse.jetty.http.HttpStatus;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.java.metadata.JavaMetadata;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

public abstract class JavaReleaseRepository extends JavaRepository 
{	
	protected Response addReleaseMetadata(JavaIndexKey key, StorageRequest sr)
	{
		try 
		{			
			// We need two copies for this to work - we can't just use the request stream twice
			// as you can't seem to reset it (makes sense)
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = new TeeInputStream(sr.getStream(), out);
			
			// Use the input to write it to disk
			addArtifactToStorage(key, StorageRequest.create(sr.getFilename(), in, sr.getLength()));
			closeAllStreams(in);
			
			// Now we need to marshal the XML and determine the current snapshot version.
			in = new ByteArrayInputStream(out.toByteArray());
		    JavaMetadata metadata = (JavaMetadata) JAXB.unmarshal(in, JavaMetadata.class);
			
		    // Once the metadata is upload - let's read it, and create an item in the index for it.
		    String version = metadata.getVersioning().getLatestReleaseVersion();
		    logger.debug("[Release] Uploading release " + version + " for " + key.toString());
		    JavaIndexKey releaseKey = new JavaIndexKey(key.getType(), key.getGroup(), key.getArtifact(), version);
		    IndexArtifact releaseIa = getStorage().generateArtifactPath(key.getType(), getPath(), releaseKey);
		    
			getIndex().addArtifact(releaseKey, releaseIa);
			
			sr.closeStream();
			closeAllStreams(in);
			return Response.ok().build();
		}
		catch(IndexException ie)
		{
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
		}
		catch(IndexConfictException ice)
		{
			return Response.status(HttpStatus.CONFLICT_409).build();
		} 
		catch (StorageException e) 
		{
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
		} 
	}
}
