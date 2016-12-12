package com.spedge.hangar.repo.java;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;

import org.eclipse.jetty.http.HttpStatus;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.java.metadata.JavaMetadata;
import com.spedge.hangar.storage.StorageRequest;

public abstract class JavaReleaseRepository extends JavaRepository
{
    protected Response addReleaseMetadata(JavaIndexKey key, StorageRequest sr)
    {
        try
        {
            // Use the input to write it to disk
            addArtifactToStorage(key, sr);
            InputStream metaStream = sr.getNewStream();
            JavaMetadata metadata = (JavaMetadata) JAXB.unmarshal(metaStream, JavaMetadata.class);
            metaStream.close();
            
            // Once the metadata is upload - let's read it, and create an item
            // in the index for it.
            String version = metadata.getVersioning().getLatestReleaseVersion();
            logger.debug("[Release] Uploading release " + version + " for " + key.toString());
            JavaIndexKey releaseKey = new JavaIndexKey(key.getType(), key.getGroup(), key.getArtifact(), version);
            IndexArtifact releaseIa = getStorage().getIndexArtifact(releaseKey, getPath());

            getIndex().addArtifact(releaseKey, releaseIa);
            return Response.ok().build();
        }
        catch (IndexException | IOException ie)
        {
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }
        catch (IndexConfictException ice)
        {
            return Response.status(HttpStatus.CONFLICT_409).build();
        }
    }
}
