package com.spedge.hangar.storage.request;

import com.spedge.hangar.config.ArtifactLanguage;

public class StorageRequestKey
{
    private ArtifactLanguage type;
    private String delimiter;
    private String path;
    
    /**
     * Contains the component parts that the Storage Layer will use
     * to decide where to store the artifact.
     *
     * @param type What kind of artifact is this?
     * @param delimiter The delimiter that allows the index to break down the path.
     * @param path The path that's been submitted by the API
     */
    public StorageRequestKey(ArtifactLanguage type, String delimiter, String path)
    {
        this.type = type;
        this.delimiter = delimiter;
        this.path = path;
    }
    
    public String getDelimiter()
    {
        return delimiter;
    }

    public String getPath()
    {
        return path;
    }
    
    public String getConvertedPath(String newDelimiter)
    {
        return path.replace(delimiter, newDelimiter);
    }
    
    public ArtifactLanguage getType()
    {
        return type;
    } 
}
