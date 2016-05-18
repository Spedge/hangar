package com.spedge.hangar.config;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.storage.IStorage;

import io.dropwizard.Configuration;

public class HangarConfiguration extends Configuration {

	//@NotEmpty
	@JsonProperty
    private IStorage storage;
	
	//@NotEmpty
	@JsonProperty
    private IIndex artifactIndex;
	
	@NotEmpty
	@JsonProperty
    private List<IRepository> repositories;
	
	public List<IRepository> getRepositories() {
		return repositories;
	}
    
   	public void setRepositories(List<IRepository> repositories) {
   		this.repositories = repositories;
   	}

	public IStorage getStorage() {
		return storage;
	}

	public void setStorage(IStorage storage) {
		this.storage = storage;
	}

	public IIndex getIndex() {
		return artifactIndex;
	}

	public void setIndex(IIndex index) {
		this.artifactIndex = index;
	}
}
