package com.spedge.hangar.index;

import com.spedge.hangar.repo.RepositoryType;

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

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((type.getLanguage() == null) ? 0 : type.getLanguage().hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        IndexKey other = (IndexKey) obj;
        if (key == null)
        {
            if (other.key != null)
            {
                return false;
            }
        }
        else if (!key.equals(other.key))
        {
            return false;
        }
        if (type.getLanguage() != other.type.getLanguage())
        {
            return false;
        }
        return true;
    }
}
