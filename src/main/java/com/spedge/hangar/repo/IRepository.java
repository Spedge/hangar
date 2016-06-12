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

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=JavaDownloadAPI.class, name="java-download"),
    @JsonSubTypes.Type(value=JavaReleaseAPI.class, name="java-release"),
    @JsonSubTypes.Type(value=JavaSnapshotAPI.class, name="java-snapshot"),
}) 
public interface IRepository {

	Map<String, HealthCheck> getHealthChecks();
	void loadRepository(HangarConfiguration configuration, Environment environment) throws StorageException;
	
	IStorage getStorage();
	IIndex getIndex();
}
