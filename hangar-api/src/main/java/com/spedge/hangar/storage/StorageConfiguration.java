package com.spedge.hangar.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class StorageConfiguration
{

    @NotEmpty
    private String uploadpath;

    @NotEmpty
    private String limit;

    @JsonProperty
    public String getUploadPath()
    {
        return uploadpath;
    }

    public void setUploadPath(String uploadpath)
    {
        this.uploadpath = uploadpath;
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
