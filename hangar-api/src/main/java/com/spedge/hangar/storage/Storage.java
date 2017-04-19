package com.spedge.hangar.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Storage implements IStorage
{
    protected final Logger logger = LoggerFactory.getLogger(Storage.class);
    protected boolean isInitalised = false;
    private StorageConfiguration sc;
    
    /**
     * Method is called by implementing classes to mark storage as ready.
     */
    protected void initialisationComplete()
    {
        isInitalised = true;  
    }
    
    /**
     * Describes if initalisationComplete() has been called on this storage.
     * @return boolean indicating if storage is implemented.
     */
    protected boolean isInitialised()
    {
        return this.isInitalised;
    }
    

    @Override
    public StorageConfiguration getStorageConfiguration()
    {
        return sc;
    }

    @Override
    @JsonProperty
    public void setStorageConfiguration(StorageConfiguration sc)
    {
        this.sc = sc;
    }
    
//    @NotEmpty
//    private String rootPath;
//
//    @JsonProperty
//    public String getRootPath()
//    {
//        return rootPath;
//    }
//
//    @JsonProperty
//    public void setRootPath(String path) throws IOException
//    {
//        this.rootPath = path;
//    }
//    
//    protected void addPathTranslator(IStorageTranslator st, String path)
//    {
//        paths.put(path, st);
//    }
//    
//    public IStorageTranslator getStorageTranslator(String prefixPath)
//    {
//        return paths.get(prefixPath);
//    }

//    @Override
//    public IndexArtifact getIndexArtifact(IndexKey key, String uploadPath) throws IndexException
//    {
//        IStorageTranslator st = paths.get(uploadPath);
//        return st.generateIndexArtifact(key, uploadPath);
//    }
}
