package com.spedge.hangar.repo.python;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.repo.RepositoryBase;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("/python")
public abstract class PythonRepository extends RepositoryBase
{
    /**
     * Returns the health checks for this repository.
     */
    public Map<String, HealthCheck> getHealthChecks()
    {
        Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
        return checks;
    }
    
    public StreamingOutput getMetadata(String[] proxies, String path)
    {
        // This should probably be requested from the index rather than PyPi
        StorageRequest sr = super.requestProxiedArtifact(proxies, path, "");
        return sr.getStreamingOutput();
    }

    public StreamingOutput getProxiedArtifact(String[] proxies, String path, PythonIndexKey key)
    {
        StorageRequest sr = super.requestProxiedArtifact(proxies, path, "");
        addArtifactToStorage(key, sr);
        return sr.getStreamingOutput();
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
