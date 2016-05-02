package com.spedge.hangar.index;

import java.util.HashMap;
import java.util.Map;


public class InMemoryIndex implements IIndex {
	
	private Map<String, IndexArtifact> index;
	
	public InMemoryIndex()
	{
		this.index = new HashMap<String, IndexArtifact>();
	}

	public boolean isArtifact(IndexKey key) {
		return index.containsKey(key.toString());
	}
	
	public void addArtifact(IndexKey key, IndexArtifact artifact)
	{
		index.put(key.toString(), artifact);
	}

	public IndexArtifact getArtifact(IndexKey key) 
	{
		IndexArtifact ia = index.get(key.toString());
		return ia;
	}

}
