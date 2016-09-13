package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.RepositoryBase;
import com.spedge.hangar.repo.java.healthcheck.JavaRepositoryHealthcheck;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

@Path("/java")
public abstract class JavaRepository extends RepositoryBase
{
    protected final Logger logger = LoggerFactory.getLogger(JavaRepository.class);

    private JavaRepositoryHealthcheck check;

    public JavaRepository()
    {
        check = new JavaRepositoryHealthcheck();
    }

    /**
     * Returns the health checks for this repository.
     */
    public Map<String, HealthCheck> getHealthChecks()
    {
        Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
        checks.put("java_repo", check);
        checks.put("java_storage", getStorage().getHealthcheck());
        return checks;
    }

    protected StreamingOutput getArtifact(JavaIndexKey key, String filename)
    {
        if (Pattern.matches("[.\\d\\.]*-SNAPSHOT", key.getVersion()))
        {
            logger.info("[Downloading Snapshot] " + key.toString());
            return getSnapshotArtifact(key, filename);
        }
        else
        {
            try
            {
                if (getIndex().isArtifact(key))
                {
                    return getStorage().getArtifactStream(getIndex().getArtifact(key), filename);
                }
                else
                {
                    throw new NotFoundException();
                }
            }
            catch (IndexException ie)
            {
                throw new InternalServerErrorException();
            }
        }
    }

    protected Response addMetadata(JavaIndexKey key, StorageRequest sr)
    {
        // Use the input to write it to disk
        IndexArtifact ia = addArtifactToStorage(key, sr);
        
        try
        {
            // Once we're happy it's there, update the index.
            getIndex().addArtifact(key, ia);
        }
        catch (IndexException ie)
        {
            return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }
        catch (IndexConfictException ice)
        {
            return Response.status(HttpStatus.CONFLICT_409).build();
        }
        return Response.ok().build();
    }

    protected Response addArtifact(JavaIndexKey key, StorageRequest sr)
    {
        // If we simply have an artifact to add that has no effect on the index,
        // go ahead and get it done.
        addArtifactToStorage(key, sr);
        return Response.ok().build();
    }

    /*
     * This concentrates on actually getting the artifact into storage. Saves
     * duplication of code.
     */
    protected IndexArtifact addArtifactToStorage(JavaIndexKey key, StorageRequest sr)
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
    
    /**
     * This version is different as we need to re-write the filename with the
     * timestamp for the latest version.
     * 
     * @param key
     *            IndexKey to find the Artifact in the Index
     * @param filename
     *            Filename from the request
     * @return StreamingOutput from the Storage Layer
     */
    protected StreamingOutput getSnapshotArtifact(JavaIndexKey key, String filename)
    {
        logger.info("[Downloading Snapshot] " + key);
        try
        {
            if (getIndex().isArtifact(key))
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
        catch (IndexException ie)
        {
            throw new InternalServerErrorException();
        }
    }
    
    public StreamingOutput getProxiedArtifact(String[] proxies, JavaIndexKey key, String filename)
    {
        try
        {
            return getArtifact(key, filename);
        }
        catch (NotFoundException nfe)
        {
            String path = key.getGroup().replace(".", "/") + "/" 
                            + key.getArtifact() + "/" 
                            + key.getVersion() + "/" 
                            + filename;
            
            StorageRequest sr = requestProxiedArtifact(proxies, path, filename);
            addArtifactToStorage(key, sr);
            
            return sr.getStreamingOutput();
        }
    }
}
