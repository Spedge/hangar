package com.spedge.hangar.repo.python;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

public class PythonIndexKey extends IndexKey
{
    private String base1;
    private String base2;
    private String baseMax;
    private String artifact;
    
    /**
     * Saves the main details for identifying a package within Pip.
     * @param type RepositoryType this index item is from
     * @param base1 Base1 of the URL
     * @param base2 Base2 of the URL
     * @param baseMax Big chunk of UUID
     * @param artifact Name of the artifact
     */
    public PythonIndexKey(RepositoryType type, String base1, String base2, String baseMax, String artifact)
    {
        super(type, artifact);
        this.artifact = artifact;
        this.base1 = base1;
        this.base2 = base2;
        this.baseMax = baseMax;
    }

    public String getArtifact()
    {
        return artifact;
    }

    public String getPath()
    {
        return "/" + base1 + "/" + base2 + "/" + baseMax + "/" + artifact;
    }
}
