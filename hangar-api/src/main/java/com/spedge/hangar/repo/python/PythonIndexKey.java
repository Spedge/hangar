package com.spedge.hangar.repo.python;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

public class PythonIndexKey extends IndexKey
{
    private String artifact;
    private String filename;
    
    /**
     * Saves the main details for identifying a package within Pip.
     * @param type RepositoryType this index item is from
     * @param artifact Name of the artifact
     * @param filename Name of the actual file being served
     */
    public PythonIndexKey(RepositoryType type, String artifact, String filename)
    {
        super(type, artifact);
        this.artifact = artifact;
        this.filename = filename;
    }

    public String getArtifact()
    {
        return artifact;
    }

    public String getPath()
    {
        return "/" + artifact + "/" + filename;
    }
}
