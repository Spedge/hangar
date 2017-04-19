package com.spedge.hangar.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class StorageConfiguration
{

    @NotEmpty
    private String rootPath;

    @NotEmpty
    private String limit;

    @JsonProperty
    public String getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(String uploadpath)
    {
        this.rootPath = uploadpath;
    }

    @JsonProperty
    public String getLimit()
    {
        return limit;
    }

    public void setLimit(String limit)
    {
        this.limit = limit;
    }
}
