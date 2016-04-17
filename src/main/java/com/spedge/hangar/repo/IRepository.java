package com.spedge.hangar.repo;

import java.util.Map;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.spedge.hangar.repo.java.JavaRepository;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "repository")
@JsonSubTypes({
    @JsonSubTypes.Type(value=JavaRepository.class, name="java"),
}) 
public interface IRepository {

	Map<String, HealthCheck> getHealthChecks();
	
}
