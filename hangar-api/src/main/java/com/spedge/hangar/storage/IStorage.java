package com.spedge.hangar.storage;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.storage.local.LocalStorage;
import com.spedge.hangar.storage.s3.S3Storage;

import java.util.List;

import javax.ws.rs.core.StreamingOutput;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "store")
@JsonSubTypes(
    { @JsonSubTypes.Type(value = LocalStorage.class, name = "local"),
      @JsonSubTypes.Type(value = S3Storage.class, name = "s3"), 
    })
public interface IStorage
{
    void initialiseStorage(IStorageTranslator st, String uploadPath) throws StorageException;
    
    HealthCheck getHealthcheck();

    List<IndexKey> getArtifactKeys(String uploadPath) throws StorageException;

    StreamingOutput getArtifactStream(IndexArtifact key, String filename);
    
    IndexArtifact getIndexArtifact(IndexKey key, String uploadPath) throws StorageException;

    void uploadReleaseArtifactStream(IndexArtifact ia, StorageRequest sr) throws StorageException;

    void uploadSnapshotArtifactStream(IndexArtifact ia, StorageRequest sr) throws StorageException;
}
