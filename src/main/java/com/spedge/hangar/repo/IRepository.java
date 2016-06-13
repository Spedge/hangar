package com.spedge.hangar.repo;

import java.util.Map;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.repo.java.api.JavaDownloadAPI;
import com.spedge.hangar.repo.java.api.JavaReleaseAPI;
import com.spedge.hangar.repo.java.api.JavaSnapshotAPI;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;

import io.dropwizard.setup.Environment;

/**
 * This is the main interface that will allow configuration of repository APIs
 * via the config functionality in Dropwizard. 
 *  
 * @author Spedge
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=JavaDownloadAPI.class, name="java-download"),
    @JsonSubTypes.Type(value=JavaReleaseAPI.class, name="java-release"),
    @JsonSubTypes.Type(value=JavaSnapshotAPI.class, name="java-snapshot"),
}) 
public interface IRepository {

	/**
	 * Each repository should provide custom healthchecks for it's own use cases.
	 * @return
	 */
	Map<String, HealthCheck> getHealthChecks();
	
	/**
	 * After initial configuration, this command is run in order to 
	 * set-up or load the repository and get it to a state where it's ready to run.
	 * @param configuration
	 * @param environment
	 * @throws StorageException
	 */
	void loadRepository(HangarConfiguration configuration, Environment environment) throws StorageException;
	
	/**
	 * Returns the storage configured for the repositories (One storage for all repos)
	 * @return
	 */
	IStorage getStorage();
	
	/**
	 * Returns the index configured for the repositories (One index for all repos)
	 * @return
	 */
	IIndex getIndex();
}
