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
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.request.StorageRequest;
import com.spedge.hangar.storage.request.StorageRequestException;

public abstract class RepositoryBase implements IRepository
{
    private IStorage storage;
    private IIndex index;
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
    public void loadRepository(IStorage storage, IIndex index)
                    throws IndexException, StorageException
    {
        this.storage = storage;
        this.index = index;
        this.createFactory();
    }

    public abstract RepositoryType getType();

    protected abstract void createFactory();

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

    /*
     * This concentrates on actually getting the artifact into storage. Saves
     * duplication of code.
     */
    protected void addArtifactToStorage(StorageRequest sr)
    {
        try
        {
            getStorage().uploadArtifactStream(sr);
        }
        catch (StorageRequestException sre)
        {
            throw new InternalServerErrorException();
        }
    }
}
