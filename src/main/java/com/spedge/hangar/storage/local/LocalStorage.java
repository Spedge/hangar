package com.spedge.hangar.storage.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.storage.IStorage;

public class LocalStorage implements IStorage
{
	private HealthCheck check = null;
	
	@NotEmpty
	private String path;
	
	@NotEmpty
	private String size;

	public HealthCheck getHealthcheck() 
	{
		if(check == null) { check = new LocalStorageHealthcheck(path, size); }
		return check;
	}

	@JsonProperty
	public String getPath() {
		return path;
	}

	@JsonProperty
	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty
	public String getSize() {
		return size;
	}

	@JsonProperty
	public void setSize(String size) {
		this.size = size;
	}

	public StreamingOutput getArtifactStream(final String artifact_path, final String artifact) {
		
		return new StreamingOutput() {
            
            public void write(OutputStream os) throws IOException, WebApplicationException {
                ByteStreams.copy(new FileInputStream(path + "/" + artifact_path + "/" + artifact), os);
            }
        };
	}
}
