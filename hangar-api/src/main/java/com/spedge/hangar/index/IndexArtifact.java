package com.spedge.hangar.index;

import java.io.Serializable;
import java.util.Map;

public abstract class IndexArtifact implements Serializable
{
	private static final long serialVersionUID = -5720959937441417671L;
	private String location;

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean isStoredFile(String filename)
	{
		for(String key : getFileTypes().keySet())
		{
			if(filename.endsWith(key)) { return getFileTypes().get(key); }
		}
		return false;
	}
	
	public void setStoredFile(String filename)
	{
		for(String key : getFileTypes().keySet())
		{
			if(filename.endsWith(key)) { getFileTypes().put(key, true); }
		}
	}
	
	protected abstract Map<String, Boolean> getFileTypes();
}
