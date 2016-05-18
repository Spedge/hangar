package com.spedge.hangar.index;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;


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

	public void load(RepositoryType type, IStorage storage, String uploadPath) throws StorageException
	{
		for(IndexKey key : storage.getArtifactKeys(type, uploadPath))
		{
			index.put(key.toString(), storage.generateArtifactPath(type, uploadPath, key));
		}
	}
}
