package com.spedge.hangar.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryLanguage;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.local.LocalStorageException;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class Storage implements IStorage
{
    protected final Logger logger = LoggerFactory.getLogger(Storage.class);

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

    @Override
    public IndexArtifact generateArtifactPath(RepositoryType type, String uploadPath, IndexKey key)
            throws LocalStorageException
    {
        IndexArtifact ia;

        if (type.getLanguage().equals(RepositoryLanguage.JAVA))
        {
            String[] split = key.toPath().split(":");
            String group = split[0];
            String artifact = (split.length > 1) ? split[1] : "";
            String version = (split.length > 2) ? split[2] : "";
            
            ia = generateJavaArtifactPath(new JavaIndexKey(type, group, artifact, version), uploadPath);
        }
        else
        {
            throw new LocalStorageException();
        }
        return ia;
    }

    // These two methods allow for the creation of Java-specific Keys and Paths
    // depending on GAV Parameters
    protected abstract IndexArtifact generateJavaArtifactPath(JavaIndexKey key, String uploadPath)
            throws LocalStorageException;
}
