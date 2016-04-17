package com.spedge.hangar.storage;

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

}
