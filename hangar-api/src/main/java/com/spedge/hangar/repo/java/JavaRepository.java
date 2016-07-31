package com.spedge.hangar.repo.java;

import com.google.common.io.ByteStreams;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.healthcheck.JavaRepositoryHealthcheck;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

import io.dropwizard.setup.Environment;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("/java")
public abstract class JavaRepository implements IRepository
{
    protected final Logger logger = LoggerFactory.getLogger(JavaRepository.class);
    private HealthCheck check;
    private IStorage storage;
    private IIndex index;
    private StorageConfiguration storageConfig;

    @NotEmpty
    private String id;

    public JavaRepository()
    {
        check = new JavaRepositoryHealthcheck();
    }

    @JsonProperty
    public String getId()
    {
        return id;
    }

    @JsonProperty
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Returns the health checks for this repository.
     */
    public Map<String, HealthCheck> getHealthChecks()
    {
        Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
        checks.put("java_repo", check);
        checks.put("java_storage", storage.getHealthcheck());
        return checks;
    }

    @Override
    public IStorage getStorage()
    {
        return storage;
    }

    @Override
    public IIndex getIndex()
    {
        return index;
    }

    public void setStorageConfiguration(StorageConfiguration storageConfig)
    {
        this.storageConfig = storageConfig;
    }

    public abstract RepositoryType getType();

    public String getPath()
    {
        return storageConfig.getUploadPath();
    }

    @Override
    public void loadRepository(HangarConfiguration configuration, Environment environment)
            throws IndexException, StorageException
    {
        storage = configuration.getStorage();
        index = configuration.getIndex();

        storage.initialiseStorage(storageConfig.getUploadPath());

        index.load(getType(), storage, storageConfig.getUploadPath());
    }

    protected StreamingOutput getArtifact(JavaIndexKey key, String filename)
    {
        if(Pattern.matches("[.\\d\\.]*-SNAPSHOT", key.getVersion()))
        {
            logger.info("[Downloading Snapshot] " + key.toString());
            return getSnapshotArtifact(key, filename);
        }
        else
        {
            try
            {
                if (index.isArtifact(key))
                {
                    return getStorage().getArtifactStream(index.getArtifact(key), filename);
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
        sr.closeStream();

        try
        {
            // Once we're happy it's there, update the index.
            index.addArtifact(key, ia);
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
        sr.closeStream();

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
            IndexArtifact ia = getStorage().generateArtifactPath(getType(), getPath(), key);

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
    
    protected StreamingOutput getProxyArtifact(String[] proxies, JavaIndexKey key, String filename)
    {
        logger.info("[Downloading Proxied Artifact] " + key);
        try
        {
            for (String source : proxies)
            {
                // So the artifact doesn't exist. We try and download it and
                // save it to disk.
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(source).path(key.getGroup().replace(".", "/") + "/" 
                                                            + key.getArtifact() + "/" 
                                                            + key.getVersion() + "/" + filename);

                Invocation.Builder builder = target.request(MediaType.WILDCARD);
                Response resp = builder.get();

                if (resp.getStatus() == HttpStatus.OK_200)
                {
                    logger.info("[Proxy] Downloading from " + source);

                    // We need to load it into memory. We'll look at doing
                    // this another way
                    // (perhaps to disk first) but I'd rather download then
                    // upload to S3 and back to the client
                    // concurrently. Not sure if this is possible but it'd
                    // save a bunch of time.
                    InputStream in = resp.readEntity(InputStream.class);

                    byte[] byteArray = IOUtils.toByteArray(in);
                    resp.close();

                    // Now upload the artifact to our proxy location.
                    StorageRequest sr = new StorageRequest();
                    sr.setFilename(filename);
                    sr.setStream(new ByteArrayInputStream(byteArray));
                    sr.setLength(resp.getLength());
                    
                    // Ok now we've downloaded the thing, we'll use it.
                    IndexArtifact ia = getStorage().generateArtifactPath(getType(), getPath(), key);
                    getStorage().uploadSnapshotArtifactStream(ia, sr);

                    // We should add it to the index now. Most of these
                    // don't have metadata, so we
                    // add it to the index as soon as we get it.
                    getIndex().addArtifact(key, ia);

                    // We take our copy and return it to the user
                    // post-upload.
                    final InputStream writer = new ByteArrayInputStream(byteArray);
                    return new StreamingOutput()
                    {

                        public void write(OutputStream os)
                                throws IOException, WebApplicationException
                        {
                            ByteStreams.copy(writer, os);
                            writer.close();
                        }
                    };
                }
            }

            throw new NotFoundException();
        }
        catch (StorageException | IndexConfictException | IndexException | IOException exp)
        {
            throw new InternalServerErrorException();
        }
    }

    /*
     * Sometimes we have multiple streams open - this is just a nice way of
     * getting round it. Really, it'd be nice if the function took an array but
     * there you go ;)
     */
    protected void closeAllStreams(InputStream... streams)
    {
        for (InputStream stream : streams)
        {
            IOUtils.closeQuietly(stream);
        }
    }
}
