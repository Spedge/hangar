package com.spedge.hangar.repo.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXB;

import org.apache.commons.io.input.TeeInputStream;
import org.eclipse.jetty.http.HttpStatus;

import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.java.metadata.JavaMetadata;

public abstract class JavaSnapshotRepository extends JavaRepository 
{
	/**
	 * This version is different as we need to re-write the filename
	 * with the timestamp for the latest version.
	 * 
	 * @param key IndexKey to find the Artifact in the Index
	 * @param filename Filename from the request
	 * @return StreamingOutput from the Storage Layer
	 */
	protected StreamingOutput getSnapshotArtifact(JavaIndexKey key, String filename) 
	{
		try 
		{
			if(getIndex().isArtifact(key))
			{
				JavaIndexArtifact ia = (JavaIndexArtifact) getIndex().getArtifact(key);
				String snapshotFilename = filename.replace(key.getVersion(), ia.getSnapshotVersion());
				return getStorage().getArtifactStream(ia, snapshotFilename);
			}
			else
			{
				throw new NotFoundException();
			}
		} 
		catch (IndexException e) 
		{
			throw new InternalServerErrorException();
		}
	}
	
	protected Response addSnapshotMetadata(JavaIndexKey key, InputStream uploadedInputStream)
	{
		try 
		{			
			// We need two copies for this to work - we can't just use the request stream twice
			// as you can't seem to reset it (makes sense)
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = new TeeInputStream(uploadedInputStream, out);
			
			// Use the input to write it to disk
			JavaIndexArtifact ia = (JavaIndexArtifact) addArtifactToStorage(key, "maven-metadata.xml", in);
			closeAllStreams(in);
			
			// Now we need to marshal the XML and determine the current snapshot version.
			in = new ByteArrayInputStream(out.toByteArray());
		    JavaMetadata metadata = (JavaMetadata) JAXB.unmarshal(in, JavaMetadata.class);
			
		    logger.debug("[Snapshot] Uploading snapshot " + metadata.getVersioning().getSnapshot().getVersion() + " for " + key.toString());
			ia.setSnapshotVersion(metadata.getVersioning().getSnapshot().getVersion());
			getIndex().addArtifact(key, ia);
			
			closeAllStreams(uploadedInputStream, in);
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
	}
}
