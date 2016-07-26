package com.spedge.hangar.index;

import java.io.Serializable;
import java.util.Map;

public abstract class IndexArtifact implements Serializable
{
    private static final long serialVersionUID = -5720959937441417671L;
    private String location;

    public String getLocation()
    {
        return this.location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * Checks if this type of file (on postfix)
     * has been stored in the storage layer.
     * 
     * @param filename Full filename to check
     * @return True if file has been stored
     */
    public boolean isStoredFile(String filename)
    {
        for (String key : getFileTypes().keySet())
        {
            if (filename.endsWith(key))
            {
                return getFileTypes().get(key);
            }
        }
        return false;
    }

    /**
     * Sets that this particular type of file has been stored
     * within the storage layer for later reference.
     * 
     * @param filename Full filename to check
     */
    public void setStoredFile(String filename)
    {
        for (String key : getFileTypes().keySet())
        {
            if (filename.endsWith(key))
            {
                getFileTypes().put(key, true);
            }
        }
    }

    protected abstract Map<String, Boolean> getFileTypes();
}
