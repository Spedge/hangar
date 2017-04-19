package com.spedge.hangar.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StartupConfiguration
{
    @JsonProperty
    private boolean reIndex = true;
    
    public boolean getReIndex()
    {
        return reIndex;
    }
}
