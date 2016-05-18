package com.spedge.hangar.storage.local;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;

public class LocalStorageHealthcheck extends HealthCheck 
{
	@NotEmpty
	private String path;
	
	public LocalStorageHealthcheck(String path) {
		this.path = path;
	}

	@Override
	protected Result check() throws Exception 
	{		
		if(Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS))
		{
			return Result.healthy();
		}
		else
		{
			return Result.unhealthy("Cannot access storage path : " + path);
		}
	}
}
