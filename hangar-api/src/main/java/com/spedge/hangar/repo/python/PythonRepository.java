package com.spedge.hangar.repo.python;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.RepositoryBase;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

@Path("/python")
public abstract class PythonRepository extends RepositoryBase
{
    /**
     * Returns the health checks for this repository.
     */
    public Map<String, HealthCheck> getHealthChecks()
    {
        Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
        
        // We want to add a healthcheck for PyPi as I'm not sure how I can get round the index request.
        return checks;
    }
    
    public StreamingOutput getMetadata(String[] proxies, String path)
    {
        // This should probably be requested from the index rather than PyPi
        StorageRequest sr = super.requestProxiedArtifact(proxies, path, "");
        
        // For each anchor entry, we want to replace any entries we have locally. 
        Document doc = Jsoup.parse(sr.toString());
        doc.outputSettings().prettyPrint(false);
        Elements links = doc.select("a");
        
        for (Element link : links)  
        {
            try
            {
                if(getIndex().isArtifact(new PythonIndexKey(RepositoryType.PROXY_PYTHON, path, link.text())))
                {
                    link.attr("href", "../../local/" + path + link.text());
                }
            }
            catch (IndexException ie)
            {
                throw new InternalServerErrorException();
            }
        }
        
        byte[] bytearray = doc.toString().getBytes();
        sr = new StorageRequest.StorageRequestBuilder().stream(bytearray).filename("").build();
        
        return sr.getStreamingOutput();
    }

    public StreamingOutput getProxiedArtifact(String[] proxies, String path, String artifact)
    {
        StorageRequest sr = super.requestProxiedArtifact(proxies, path + artifact, "");
        
        
        //addArtifactToStorage(key, sr);
        return sr.getStreamingOutput();
    }
    
    public StreamingOutput getLocalArtifact(String[] proxies, PythonIndexKey pk)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    protected Response addArtifact(PythonIndexKey key, StorageRequest sr)
    {
        // If we simply have an artifact to add that has no effect on the index,
        // go ahead and get it done.
        addArtifactToStorage(key, sr);
        return Response.ok().build();
    }
    
    protected IndexArtifact addArtifactToStorage(PythonIndexKey key, StorageRequest sr)
    {
        // Artifacts are uploaded, but for them to become live they need
        // metadata uploaded.
        // All this does is save it successfully.
        try
        {
            // Create the entry for the index that contains current information
            // about the artifact.
            IndexArtifact ia = getStorage().getIndexArtifact(key, getPath());

            // Upload the file we need.
            getStorage().uploadSnapshotArtifactStream(ia, sr);

            return ia;
        }
        catch (StorageException se)
        {
            throw new InternalServerErrorException();
        }
    }



    

}
