package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.InMemoryIndex;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.storage.IStorage;

@Path("/java")
public abstract class JavaRepository implements IRepository
{
	final static Logger logger = LoggerFactory.getLogger(JavaRepository.class);
	private HealthCheck check;
	private IIndex index;
	
	@NotEmpty
	private String id;

	@NotNull
	private IStorage storage;
	
	public JavaRepository()
	{
		check = new JavaRepositoryHealthcheck();
		setIndex(new InMemoryIndex());
	}
	
	@JsonProperty
	public String getId() {
		return id;
	}

	@JsonProperty
	public void setId(String id) {
		this.id = id;
	}
	
	@JsonProperty
	public IStorage getStorage() {
		return storage;
	}

	@JsonProperty
	public void setStorage(IStorage storage) {
		this.storage = storage;
	}
		
	public IIndex getIndex() {
		return index;
	}

	public void setIndex(IIndex index) {
		this.index = index;
	}

	public Map<String, HealthCheck> getHealthChecks() {
		Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
		checks.put("java_repo", check);
		checks.put("java_storage", storage.getHealthcheck());
		return checks;
	}
}
