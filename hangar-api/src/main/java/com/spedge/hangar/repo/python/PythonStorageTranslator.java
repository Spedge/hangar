package com.spedge.hangar.repo.python;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.IStorageTranslator;

public class PythonStorageTranslator implements IStorageTranslator
{
    private final String[] delimiters = new String[]{};
    private RepositoryType type;
    
    public PythonStorageTranslator(RepositoryType type)
    {
        this.type = type;
    }
    
    @Override
    public String[] getDelimiters()
    {
        return delimiters;
    }

    @Override
    public IndexKey generateIndexKey(String prefixPath, String prefix) throws IndexException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IndexArtifact generateIndexArtifact(IndexKey key, String uploadPath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RepositoryType getType()
    {
        return type;
    }
}
