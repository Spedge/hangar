package com.spedge.hangar.repo.java.index;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

import java.util.Objects;

public class JavaIndexKey extends IndexKey
{
    private String group = "";
    private String artifact = "";
    private String version = "";

    /**
     * <p>Creates a Key to be used to register an IndexArtifact with
     * the Index layer. Formats a full string into GAV using : as delimiter
     * 
     * Example : com.spedge:hangar-artifact:1.0.0 </p>
     * 
     * @param type Type of Repository this Artifact will be stored from.
     * @param key String to be reformated.
     */
    public JavaIndexKey(RepositoryType type, String key)
    {
        super(type, key);

        String[] split = key.split(":");

        this.group = split[0];
        this.artifact = (split.length > 1) ? split[1] : "";
        this.version = (split.length > 2) ? split[2] : "";
    }

    /**
     * <p>Creates a Key to be used to register an IndexArtifact with
     * the Index layer. </p>
     * 
     * @param type Type of Repository this Artifact will be stored from.
     * @param group Group of the Artifact
     * @param artifact Name of the Artifact
     * @param version Version of the Artifact
     */
    public JavaIndexKey(RepositoryType type, String group, String artifact, String version)
    {
        super(type, group + ":" + artifact + ":" + version);

        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public String getGroup()
    {
        return group;
    }

    public String getArtifact()
    {
        return artifact;
    }

    public String getVersion()
    {
        return version;
    }

    public String toString()
    {
        return super.toString();
    }

    @Override
    public boolean equals(Object object)
    {
        // self check
        if (this == object) 
        {
            return true;
        }
        // null check
        if (object == null)
        {
            return false;
        }
        // type check and cast
        if (getClass() != object.getClass())
        {
            return false;
        }
        
        JavaIndexKey jik = (JavaIndexKey) object;

        return Objects.equals(toString(), jik.toString())
                && Objects.equals(getType(), jik.getType());
    }
}
