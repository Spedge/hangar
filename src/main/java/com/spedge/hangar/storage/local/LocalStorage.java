package com.spedge.hangar.storage.local;

import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
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
}
