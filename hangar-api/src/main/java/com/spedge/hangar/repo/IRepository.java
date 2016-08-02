package com.spedge.hangar.repo;

import java.util.Map;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.java.api.JavaDownloadEndpoint;
import com.spedge.hangar.repo.java.api.JavaReleaseEndpoint;
import com.spedge.hangar.repo.java.api.JavaSnapshotEndpoint;
import com.spedge.hangar.repo.python.api.PythonDownloadEndpoint;
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
@JsonSubTypes(
    { @JsonSubTypes.Type(value = JavaDownloadEndpoint.class, name = "java-download"),
      @JsonSubTypes.Type(value = JavaReleaseEndpoint.class, name = "java-release"),
      @JsonSubTypes.Type(value = JavaSnapshotEndpoint.class, name = "java-snapshot"),
      @JsonSubTypes.Type(value = PythonDownloadEndpoint.class, name = "python-download")
    })
public interface IRepository
{

    /**
     * Each repository should provide custom healthchecks for it's own use
     * cases.
     * 
     * @return An map of healthchecks
     */
    Map<String, HealthCheck> getHealthChecks();

    /**
     * After initial configuration, this command is run in order to set-up or
     * load the repository and get it to a state where it's ready to run.
     * 
     * @param configuration HangarConfiguration passed in as YAML
     * @param environment Dropwizard environment
     * @throws StorageException Thrown when Storage initialisation fails.
     * @throws IndexException Thrown when Index initialisation fails.
     */
    void loadRepository(HangarConfiguration configuration, Environment environment)
            throws StorageException, IndexException;

    /**
     * Retrieves the storage configured for the repositories (One storage for all
     * repos).
     * 
     * @return Returns the storage for this repository
     */
    IStorage getStorage();

    /**
     * Returns the index configured for the repositories (One index for all
     * repos).
     * 
     * @return Returns the index for this repository
     */
    IIndex getIndex();
}
