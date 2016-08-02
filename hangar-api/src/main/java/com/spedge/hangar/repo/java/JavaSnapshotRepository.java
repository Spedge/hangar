package com.spedge.hangar.repo.java;

import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.java.metadata.JavaMetadata;
import com.spedge.hangar.storage.StorageRequest;

import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;

public abstract class JavaSnapshotRepository extends JavaRepository
{
    protected Response addSnapshotMetadata(JavaIndexKey key, StorageRequest sr)
    {
        try
        {            
            final JavaIndexArtifact ia = (JavaIndexArtifact) addArtifactToStorage(key, sr);      

            // Now we need to marshal the XML and determine the current snapshot version.       
            InputStream metaStream = sr.getNewStream();     
            JavaMetadata metadata = (JavaMetadata) JAXB.unmarshal(metaStream, JavaMetadata.class);      
            metaStream.close();
            
            logger.debug("[Snapshot] Uploading snapshot " 
                         + metadata.getVersioning().getSnapshot().getVersion() 
                         + " for " + key.toString());      
            
            ia.setSnapshotVersion(metadata.getVersioning().getSnapshot().getVersion());     
            getIndex().addArtifact(key, ia);        
       
            return Response.ok().build();
        }
        catch (IOException | IndexException ie)
        {
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }
        catch (IndexConfictException ice)
        {
            return Response.status(HttpStatus.CONFLICT_409).build();
        }
    }
}
