package com.spedge.hangar.storage.s3;

import com.codahale.metrics.health.HealthCheck;

import org.hibernate.validator.constraints.NotEmpty;

public class S3StorageHealthcheck extends HealthCheck
{
    @NotEmpty
    private String path;

    public S3StorageHealthcheck(String path)
    {
        this.path = path;
    }

    @Override
    protected Result check() throws Exception
    {
        return Result.healthy();
    }
}
