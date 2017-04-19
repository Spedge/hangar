package com.spedge.hangar.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.storage.IStorage;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class HangarConfiguration extends Configuration
{
    @JsonProperty
    private IStorage storage;

    @JsonProperty
    private IIndex artifactIndex;
    
    @JsonProperty
    private StartupConfiguration onStartup;

    @NotEmpty
    @JsonProperty
    private List<IRepository> repositories;

    public List<IRepository> getRepositories()
    {
        return repositories;
    }

    public void setRepositories(List<IRepository> repositories)
    {
        this.repositories = repositories;
    }

    public IStorage getStorage()
    {
        return storage;
    }

    public void setStorage(IStorage storage)
    {
        this.storage = storage;
    }

    public IIndex getIndex()
    {
        return artifactIndex;
    }

    public void setIndex(IIndex index)
    {
        this.artifactIndex = index;
    }

    public StartupConfiguration getOnStartup()
    {
        return onStartup;
    }
}
