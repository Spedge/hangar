package com.spedge.hangar.repo.java;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.IRepository;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.healthcheck.JavaRepositoryHealthcheck;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

import io.dropwizard.setup.Environment;

@Path("/java")
public abstract class JavaRepository implements IRepository
{
	protected final static Logger logger = LoggerFactory.getLogger(JavaRepository.class);
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
	public void loadRepository(HangarConfiguration configuration, Environment environment) throws IndexException, StorageException 
	{
		storage = configuration.getStorage();
		index = configuration.getIndex();
		
		storage.initialiseStorage(storageConfig.getUploadPath());
		
		index.load(getType(), storage, storageConfig.getUploadPath());
	}
	
	protected StreamingOutput getArtifact(JavaIndexKey key, String filename) 
	{
		try 
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
		catch (IndexException e) 
		{
			throw new InternalServerErrorException();
		}
	}	
	
	protected Response addMetadata(JavaIndexKey key, StorageRequest sr)
	{
		// Use the input to write it to disk
		IndexArtifact ia = addArtifactToStorage(key, sr);
		sr.closeStream();
		
		try
		{
			// Once we're happy it's there, update the index.
			index.addArtifact(key, ia);
		}
		catch(IndexException ie)
		{
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
		}
		catch(IndexConfictException ice)
		{
			return Response.status(HttpStatus.CONFLICT_409).build();
		} 
		return Response.ok().build();
	}
		
	protected Response addArtifact(JavaIndexKey key, StorageRequest sr) 
	{
		// If we simply have an artifact to add that has no effect on the index, go ahead and get it done.
		addArtifactToStorage(key, sr);
		sr.closeStream();
		
		return Response.ok().build();
	}
	
	/*
	 * This concentrates on actually getting the artifact into storage. Saves duplication of code.
	 */
	protected IndexArtifact addArtifactToStorage(JavaIndexKey key, StorageRequest sr) {
		// Artifacts are uploaded, but for them to become live they need metadata uploaded.
		// All this does is save it successfully.
		try 
		{
			// Create the entry for the index that contains current information about the artifact.
			IndexArtifact ia = getStorage().generateArtifactPath(getType(), getPath(), key);
			
			// Upload the file we need.
			getStorage().uploadSnapshotArtifactStream(ia, sr);

			return ia;
		} 
		catch (StorageException e) 
		{
			throw new InternalServerErrorException();
		} 
		
	}
	
	/*
	 * Sometimes we have multiple streams open - this is just a nice way of getting round it.
	 * Really, it'd be nice if the function took an array but there you go ;)
	 */
	protected void closeAllStreams(InputStream... streams)
	{
		for(InputStream stream : streams)
		{
			IOUtils.closeQuietly(stream);
		}
	}
}
