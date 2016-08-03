package com.spedge.hangar.repo;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

import io.dropwizard.setup.Environment;

public abstract class RepositoryBase implements IRepository
{
    private IStorage storage;
    private IIndex index;
    private StorageConfiguration storageConfig;
    protected final Logger logger = LoggerFactory.getLogger(RepositoryBase.class);
    
    @NotEmpty
    private String id;
    
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
    
    @Override
    public void loadRepository(HangarConfiguration configuration, Environment environment)
            throws IndexException, StorageException
    {
        storage = configuration.getStorage();
        index = configuration.getIndex();

        storage.initialiseStorage(storageConfig.getUploadPath());

        index.load(getType(), storage, storageConfig.getUploadPath());
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
    public IStorage getStorage()
    {
        return storage;
    }

    @Override
    public IIndex getIndex()
    {
        return index;
    }
    
    protected StorageRequest requestProxiedArtifact(String[] proxies, String path, String filename)
    {
        try
        {
            for (String source : proxies)
            {
                logger.info("[Downloading Proxied Artifact] " + source + path);
                
                // So the artifact doesn't exist. We try and download it and
                // save it to disk.
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(source).path(path);

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
                    sr.setStream(byteArray);
                    sr.setLength(resp.getLength());
                    
                    return sr;
                }
            }
            throw new NotFoundException();
        }
        catch (IOException exp)
        {
            throw new InternalServerErrorException();
        }
    }
}
