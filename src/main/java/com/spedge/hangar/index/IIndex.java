package com.spedge.hangar.index;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "index")
@JsonSubTypes({
    @JsonSubTypes.Type(value=InMemoryIndex.class, name="in-memory"),
}) 
public interface IIndex {

	// This allows us to confirm if we have an artifact of that signature
	// in the index that we retain of all uploaded files.
	//
	// Each different dependency management system will have a different
	// format for a key - i.e. Maven uses GAV (Group, Artifact, Version)
	boolean isArtifact(IndexKey key);
	
	// We can add an artifact to the index like this.
	void addArtifact(IndexKey key, IndexArtifact artifact) throws IndexConfictException;

	// Once we're happy with the artifact we want to get,
    // we need details of where to stream it from.
	IndexArtifact getArtifact(IndexKey key);

	// When the index is empty on startup, we load it 
	// from the appropriate storage.
	void load(RepositoryType type, IStorage storage, String path) throws StorageException;

	// This allows a upload to reserve a path before uploading to it.
	ReservedArtifact addReservationKey(JavaIndexKey key);

	// This allows the update to the reservation to the completed location.
	void addReservedArtifact(JavaIndexKey key, ReservedArtifact reservation, IndexArtifact ia) throws IndexConfictException;
}
