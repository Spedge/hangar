package com.spedge.hangar.storage;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

public interface IStorageTranslator
{
    String[] getDelimiters();

    IndexKey generateIndexKey(String prefixPath, String prefix) throws IndexException;

    IndexArtifact generateIndexArtifact(IndexKey key, String uploadPath);

    RepositoryType getType();
}
