package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Path;

import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.storage.IStorage;

@Path("/java")
public class JavaRepository implements IRepository
{
	private HealthCheck check;
	private static final String NAME = "java";
	
	@Valid
	@NotEmpty
	private IStorage storage;

	public JavaRepository()
	{
		check = new JavaRepositoryHealthcheck();
	}
	
	@JsonProperty
	public IStorage getStorage() {
		return storage;
	}

	@JsonProperty
	public void setStorage(IStorage storage) {
		this.storage = storage;
	}
	
	public Map<String, HealthCheck> getHealthChecks() {
		Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
		checks.put("java_repo", check);
		checks.put("java_storage", storage.getHealthcheck());
		return checks;
	}
}
