package com.spedge.hangar.storage;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.storage.local.LocalStorage;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value=LocalStorage.class, name="local"),
}) 
public interface IStorage {
	
	HealthCheck getHealthcheck();
	
	StreamingOutput getArtifactStream(String path, String artifact);
	Response uploadReleaseArtifactStream(String artifact_path, String artifact_name, InputStream uploadedInputStream);
	Response uploadSnapshotArtifactStream(String artifact_path,	String artifact_name, InputStream uploadedInputStream);

}
