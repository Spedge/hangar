package com.spedge.hangar.config;

import io.dropwizard.Configuration;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.repo.IRepository;

public class HangarConfiguration extends Configuration {

	@NotEmpty
	@JsonProperty
    private List<IRepository> repositories;
	
	public List<IRepository> getRepositories() {
		return repositories;
	}
    
   	public void setRepositories(List<IRepository> repositories) {
   		this.repositories = repositories;
   	}
}
