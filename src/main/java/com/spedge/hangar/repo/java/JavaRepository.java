package com.spedge.hangar.repo.java;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.healthcheck.JavaRepositoryHealthcheck;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageException;

import io.dropwizard.setup.Environment;

@Path("/java")
public abstract class JavaRepository implements IRepository
{
	final static Logger logger = LoggerFactory.getLogger(JavaRepository.class);
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
	public String getId() {
		return id;
	}

	@JsonProperty
	public void setId(String id) {
		this.id = id;
	}
			
	public Map<String, HealthCheck> getHealthChecks() {
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
	public void loadRepository(HangarConfiguration configuration, Environment environment) throws StorageException 
	{
		storage = configuration.getStorage();
		index = configuration.getIndex();
		index.load(getType(), storage, storageConfig.getUploadPath());
	}
	
	protected StreamingOutput getArtifact(JavaIndexKey key, String filename) 
	{
		if(index.isArtifact(key))
		{
			return getStorage().getArtifactStream(index.getArtifact(key), filename);
		}
		else
		{
			throw new NotFoundException();
		}
	}	
	
	protected Response addArtifact(JavaIndexKey key, String filename, InputStream uploadedInputStream)
	{
		try 
		{
			IndexArtifact ia = getStorage().generateArtifactPath(getType(), getPath(), key);
			getIndex().addArtifact(key, ia);
			getStorage().uploadSnapshotArtifactStream(ia, filename, uploadedInputStream);
			return Response.ok().build();
		} 
		catch (StorageException e) 
		{
			throw new InternalServerErrorException();
		}
	}
}
