package com.spedge.hangar.storage;

import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.storage.local.LocalStorage;
import com.spedge.hangar.storage.request.StorageRequest;
import com.spedge.hangar.storage.request.StorageRequestException;
import com.spedge.hangar.storage.request.StorageRequestKey;
import com.spedge.hangar.storage.s3.S3Storage;

/**
 * IStorage is an interface that describes the abstraction of the storage layer.
 * 
 * <p>
 * There are some basic things we need to get from the storage layer - the ability
 * to request a stream of an existing file, the ability to save a stream to a location,
 * to mark a file for deletion - which, depending on the storage, may consist of moving
 * a file to another location for garbage collection later. 
 * </p>
 * 
 * <p>
 * The key part is that all storage is done via a StorageRequest. It does not need 
 * to know about the index or the language of the artefact that it's been asked to save.
 * </p>
 * 
 * <p>
 * Finally - we want the ability to confirm the storage is still there and accessible, so
 * we also need an internal healthcheck. This will vary depending on the source.
 * </p>
 * 
 * @author Spedge
 *
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "store")
@JsonSubTypes(
    { 
      @JsonSubTypes.Type(value = LocalStorage.class, name = "local"),
      @JsonSubTypes.Type(value = S3Storage.class, name = "s3")
    }
)
public interface IStorage
{
    @JsonProperty
    StorageConfiguration getStorageConfiguration();
    
    @JsonProperty
    void setStorageConfiguration(StorageConfiguration sc);
    
    /**
     * Call this method to initialise the storage - Storage should not be used
     * until this method has been called and this method should set isInitalised to true once complete.
     * 
     * @throws StorageInitalisationException Exception thrown on unknown failure of Storage being Initialised
     */
    void initialiseStorage() throws StorageInitalisationException;
    
    /**
     * Confirms that the storage is still available and healthy. 
     * @return Healthcheck Object to be aggregated at a higher level.
     */
    HealthCheck getHealthcheck();
    
    /**
     * Retrieve all the keys for a particular path
     * @param sr A StorageRequest defining the path to refer to for these keys.
     * @return A list of all the potential keys under this path.
     * @throws StorageRequestException Exception on unknown failure of retrieval of keys
     */
    List<StorageRequestKey> getArtifactKeys(StorageRequest sr) throws StorageRequestException;
    
    /**
     * Retrieves a Stream for the Artifact that has been requested.
     * @param sr StorageRequest for a specific Artifact to retrieve.
     * @return StreamingOutput to download the Artifact from Storage
     */
    StreamingOutput getArtifactStream(StorageRequest sr) throws StorageRequestException;
    
    /** 
     * Takes the data in the StorageRequest and uploads it to the correct path.
     * @param sr StorageRequest for a specific Artifact to upload.
     */
    void uploadArtifactStream(StorageRequest sr) throws StorageRequestException;
    
    /**
     * Marks an Artifact for deletion on next round of Garbage Collection
     * @param sr StorageRequest for a specific Artifact to remove.
     */
    void removeArtifact(StorageRequest sr) throws StorageRequestException;
}
