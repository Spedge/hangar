package com.spedge.hangar.index;

public interface IIndex {

	// This allows us to confirm if we have an artifact of that signature
	// in the index that we retain of all uploaded files.
	//
	// Each different dependency management system will have a different
	// format for a key - i.e. Maven uses GAV (Group, Artifact, Version)
	boolean isArtifact(IndexKey key);
	
	// We can add an artifact to the index like this.
	void addArtifact(IndexKey key, IndexArtifact artifact);

	// Once we're happy with the artifact we want to get,
    // we need details of where to stream it from.
	IndexArtifact getArtifact(IndexKey key);
}
