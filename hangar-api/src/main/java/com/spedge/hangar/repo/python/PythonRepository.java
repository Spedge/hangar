package com.spedge.hangar.repo.python;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.repo.RepositoryBase;
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
        return checks;
    }

    public StreamingOutput getProxiedArtifact(String[] proxies, PythonIndexKey key, String artifact)
    {
        StorageRequest sr = super.requestProxiedArtifact(proxies, "/" + artifact + "json", key, "");
        return sr.getStreamingOutput();
    }
}
