package com.spedge.hangar.storage.s3;

import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;

public class S3StorageHealthcheck extends HealthCheck 
{
	@NotEmpty
	private String path;
	
	public S3StorageHealthcheck(String path) {
		this.path = path;
	}

	@Override
	protected Result check() throws Exception 
	{		
		return Result.healthy();
	}
}
