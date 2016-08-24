package com.spedge.hangar.index;

import com.spedge.hangar.repo.RepositoryType;

import java.util.Objects;

public class IndexKey
{
    private String key;
    private RepositoryType type = RepositoryType.UNKNOWN;

    public IndexKey(RepositoryType type, String key)
    {
        this.key = key;
        this.type = type;
    }

    public RepositoryType getType()
    {
        return type;
    }

    public String toPath()
    {
        return key;
    }

    public String toString()
    {
        return type.getLanguage() + ":" + key;
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
        
        IndexKey ik = (IndexKey) object;
        
        return Objects.equals(toString(), ik.toString()) 
            && Objects.equals(getType().getLanguage(), ik.getType().getLanguage());
    }
    
    @Override
    public int hashCode()
    {
        return (int) type.getLanguage().hashCode() + key.hashCode();
    }
}
