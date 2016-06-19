package com.spedge.hangar.storage;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.local.LocalStorage;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "store")
@JsonSubTypes({
    @JsonSubTypes.Type(value=LocalStorage.class, name="local"),
}) 
public interface IStorage {
	
	HealthCheck getHealthcheck();
	
	List<IndexKey> getArtifactKeys(RepositoryType type, String uploadPath) throws StorageException;
	IndexArtifact generateArtifactPath(RepositoryType type, String uploadPath, IndexKey key) throws StorageException;
	
	StreamingOutput getArtifactStream(IndexArtifact key, String filename);
	void uploadReleaseArtifactStream(IndexArtifact key, String filename, InputStream uploadedInputStream) throws StorageException;
	void uploadSnapshotArtifactStream(IndexArtifact key, String filename, InputStream uploadedInputStream) throws StorageException;

	void initialiseStoragePath(String uploadPath) throws StorageException;
}
