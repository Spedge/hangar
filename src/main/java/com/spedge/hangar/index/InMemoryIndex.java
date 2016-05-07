package com.spedge.hangar.index;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;


public class InMemoryIndex implements IIndex {
	
	private static InMemoryIndex instance;
	private Map<String, IndexArtifact> index;
	
	private InMemoryIndex()
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

	public void load(IStorage storage) throws StorageException
	{
		for(IndexKey key : storage.getArtifactKeys())
		{
			index.put(key.toString(), storage.generateArtifactPath(key));
		}
	}

	public static IIndex getInstance() 
	{
		if(instance == null)
		{
			instance = new InMemoryIndex();
		}
		return instance;
	}
}
