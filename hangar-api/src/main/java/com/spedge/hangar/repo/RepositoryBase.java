package com.spedge.hangar.repo;

import io.dropwizard.setup.Environment;

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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.IStorageTranslator;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

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

        storage.initialiseStorage(getStorageTranslator(), storageConfig.getUploadPath());

        index.load(getType(), storage, storageConfig.getUploadPath());
    }

    protected abstract IStorageTranslator getStorageTranslator();

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
                // So the artifact doesn't exist. We try and download it and
                // save it to disk.
            	ClientConfig configuration = new ClientConfig();
            	configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 60000);
            	configuration = configuration.property(ClientProperties.READ_TIMEOUT, 60000);
            	
                Client client = ClientBuilder.newClient(configuration);
                WebTarget target = client.target(source).path(path);
                
                logger.info("[Downloading Proxied Artifact] " + target.getUri());

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
                    return new StorageRequest.StorageRequestBuilder()
                                    .filename(filename)
                                    .stream(byteArray)
                                    .length(resp.getLength())
                                    .build();
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
