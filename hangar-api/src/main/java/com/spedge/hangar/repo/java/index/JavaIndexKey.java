package com.spedge.hangar.repo.java.index;

import java.util.Objects;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.base.JavaGroup;

public class JavaIndexKey extends IndexKey
{
    private JavaGroup group;
    private String artifact = "";
    private String version = "";

    /**
     * <p>Creates a Key to be used to register an IndexArtifact with
     * the Index layer. </p>
     * 
     * @param type Type of Repository this Artifact will be stored from.
     * @param group Group of the Artifact
     * @param artifact Name of the Artifact
     * @param version Version of the Artifact
     */
    public JavaIndexKey(RepositoryType type, JavaGroup group, String artifact, String version)
    {
        super(type, group + ":" + artifact + ":" + version);

        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public JavaGroup getGroup()
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
        // parent check
        if (! super.equals(object)) 
        {
            return false;
        }
        
        JavaIndexKey jik = (JavaIndexKey) object;
        return Objects.equals(toString(), jik.toString()) 
            && Objects.equals(getGroup(), jik.getGroup())
            && Objects.equals(getArtifact(), jik.getArtifact())
            && Objects.equals(getVersion(), jik.getVersion());
    }
    
    @Override
    public int hashCode()
    {
        return (int) group.hashCode() * artifact.hashCode() * version.hashCode() * super.hashCode();
    }
}
