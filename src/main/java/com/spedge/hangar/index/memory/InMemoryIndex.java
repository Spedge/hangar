package com.spedge.hangar.index.memory;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.index.ReservedArtifact;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
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
	
	public void addArtifact(IndexKey key, IndexArtifact artifact) throws IndexConfictException
	{
		if(index.containsKey(key.toString()))
		{
			if(!(index.get(key.toString()) instanceof ReservedArtifact))
			{
				index.put(key.toString(), artifact);
			}
			else
			{
				throw new IndexConfictException();
			}
		}
		else
		{
			index.put(key.toString(), artifact);
		}
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

	@Override
	public ReservedArtifact addReservationKey(JavaIndexKey key) 
	{
		ReservedArtifact reservation = new ReservedArtifact();
		index.put(key.toString(), reservation);
		return reservation;
	}

	@Override
	public void addReservedArtifact(JavaIndexKey key, ReservedArtifact reservation, IndexArtifact ia) throws IndexConfictException 
	{
		if(index.get(key.toString()).equals(reservation))
		{
			index.put(key.toString(), ia);
		}
		else
		{
			throw new IndexConfictException();
		}
	}
}