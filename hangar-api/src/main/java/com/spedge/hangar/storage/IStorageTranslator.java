package com.spedge.hangar.storage;

import java.nio.file.Path;
import java.util.List;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.local.LocalStorageException;

public interface IStorageTranslator
{
    String[] getDelimiters();

    IndexKey generateIndexKey(String prefixPath, String prefix) throws IndexException;

    IndexArtifact generateIndexArtifact(IndexKey key, String uploadPath) throws IndexException;

    RepositoryType getType();

    List<IndexKey> getLocalStorageKeys(Path sourcePath) throws LocalStorageException;
}
