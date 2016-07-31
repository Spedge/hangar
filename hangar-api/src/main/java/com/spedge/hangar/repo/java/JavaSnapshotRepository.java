package com.spedge.hangar.repo.java;

import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.java.metadata.JavaMetadata;
import com.spedge.hangar.storage.StorageRequest;

import org.apache.commons.io.input.TeeInputStream;
import org.eclipse.jetty.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXB;

public abstract class JavaSnapshotRepository extends JavaRepository
{
    protected Response addSnapshotMetadata(JavaIndexKey key, StorageRequest sr)
    {
        try
        {
            // We need two copies for this to work - we can't just use the request stream twice     
            // as you can't seem to reset it (makes sense)      
            ByteArrayOutputStream out = new ByteArrayOutputStream();        
            InputStream in = new TeeInputStream(sr.getStream(), out);       
            
            // Use the input to write it to disk        
            StorageRequest msr = StorageRequest.create(sr.getFilename(), in, sr.getLength());
            final JavaIndexArtifact ia = (JavaIndexArtifact) addArtifactToStorage(key, msr);      
            closeAllStreams(in);        
           
            // Now we need to marshal the XML and determine the current snapshot version.       
            in = new ByteArrayInputStream(out.toByteArray());       
            JavaMetadata metadata = (JavaMetadata) JAXB.unmarshal(in, JavaMetadata.class);      
          
            logger.debug("[Snapshot] Uploading snapshot " 
                         + metadata.getVersioning().getSnapshot().getVersion() 
                         + " for " + key.toString());      
            
            ia.setSnapshotVersion(metadata.getVersioning().getSnapshot().getVersion());     
            getIndex().addArtifact(key, ia);        
           
            sr.closeStream();       
            closeAllStreams(in);        
            return Response.ok().build();
        }
        catch (IndexException ie)
        {
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }
        catch (IndexConfictException ice)
        {
            return Response.status(HttpStatus.CONFLICT_409).build();
        }
    }
}
