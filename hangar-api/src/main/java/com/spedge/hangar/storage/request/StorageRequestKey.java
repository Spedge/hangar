package com.spedge.hangar.storage.request;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StorageRequestKey
{
    private List<String> index;
    private String filename;

    public StorageRequestKey(List<String> index, String filename)
    {
        this.index = index.stream().filter(item -> !item.isEmpty()).collect(Collectors.toList());
        this.filename = filename;
    }

    public String[] getFullKey()
    {
        List<String> tempIndex = new ArrayList<String>(index);
        
        if (filename != null) 
        { 
            tempIndex.add(filename); 
        }
        
        return tempIndex.toArray(new String[0]);
    }

    public String getFilename()
    {
        return filename;
    }

    public String getKey(String delimiter)
    {
        return String.join(delimiter, index);
    }
    
    public List<String> getKey()
    {
        return index;
    }
}
