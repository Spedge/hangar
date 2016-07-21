package com.spedge.hangar.storage.local;

import com.codahale.metrics.health.HealthCheck;

import org.hibernate.validator.constraints.NotEmpty;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class LocalStorageHealthcheck extends HealthCheck
{
    @NotEmpty
    private String path;

    public LocalStorageHealthcheck(String path)
    {
        this.path = path;
    }

    @Override
    protected Result check() throws Exception
    {
        if (Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS))
        {
            return Result.healthy();
        }
        else
        {
            return Result.unhealthy("Cannot access storage path : " + path);
        }
    }
}
