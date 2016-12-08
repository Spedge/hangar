package com.spedge.hangar.storage;

import java.io.IOException;
import java.util.HashMap;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;

public abstract class Storage implements IStorage
{
    protected final Logger logger = LoggerFactory.getLogger(Storage.class);
    private HashMap<String, IStorageTranslator> paths = new HashMap<String, IStorageTranslator>();
    
    @NotEmpty
    private String path;

    @JsonProperty
    public String getPath()
    {
        return path;
    }

    @JsonProperty
    public void setPath(String path) throws IOException
    {
        this.path = path;
    }
    
    protected void addPathTranslator(IStorageTranslator st, String path)
    {
        paths.put(path, st);
    }
    
    public IStorageTranslator getStorageTranslator(String prefixPath)
    {
        return paths.get(prefixPath);
    }

    @Override
    public IndexArtifact getIndexArtifact(IndexKey key, String uploadPath) throws IndexException
    {
        IStorageTranslator st = paths.get(uploadPath);
        return st.generateIndexArtifact(key, uploadPath);
    }
}
