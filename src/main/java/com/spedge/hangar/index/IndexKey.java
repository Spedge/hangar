package com.spedge.hangar.index;

import com.spedge.hangar.repo.RepositoryType;

public class IndexKey {

	private String key;
	private RepositoryType type = RepositoryType.JAVA;
	
	public IndexKey(RepositoryType type, String key)
	{
		this.key = key;
		this.type = type;
	}
	
	public String toPath() { 
		return key;
	}

	public String toString() {
		return type + ":" + key;
	}	
}
