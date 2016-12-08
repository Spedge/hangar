package com.spedge.hangar.repo.python;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.index.IndexArtifact;

public class PythonIndexArtifact extends IndexArtifact
{
    private static final long serialVersionUID = -4753077995717747099L;
    
    public PythonIndexArtifact(String location)
    {
        super(location);
    }
    
    @Override
    protected Map<String, Boolean> getFileTypes()
    {
        return new HashMap<String, Boolean>();
    }

}
