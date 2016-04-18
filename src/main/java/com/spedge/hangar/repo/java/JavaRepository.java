package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.storage.IStorage;

@Path("/java")
public class JavaRepository implements IRepository
{
	final static Logger logger = LoggerFactory.getLogger(JavaRepository.class);
	private HealthCheck check;
	
	@NotNull
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
	
	@GET
	@Path("{path : .+}/{artifact : [^/]+}")
	public StreamingOutput getArtifact(@PathParam("path") String path, @PathParam("artifact") String artifact)
	{
		logger.debug("Path : " + path + ", Artifact : " + artifact);
		return storage.getArtifactStream(path, artifact);
	}
}
