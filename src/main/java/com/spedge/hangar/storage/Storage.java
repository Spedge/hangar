package com.spedge.hangar.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaIndexKey;
import com.spedge.hangar.storage.local.LocalStorageException;

public abstract class Storage implements IStorage
{
	protected final static Logger logger = LoggerFactory.getLogger(Storage.class);
	protected RepositoryType type = RepositoryType.UNKNOWN;

	@Override
	public void setType(RepositoryType type) {
		this.type = type;
	}
		
	@Override
	public IndexArtifact generateArtifactPath(IndexKey key) throws LocalStorageException 
	{
		IndexArtifact ia;
		
		if(type.equals(RepositoryType.JAVA)) { ia = generateJavaArtifactPath(new JavaIndexKey(key.toPath())); }
		else throw new LocalStorageException();
		
		return ia;
	}
	
	// These two methods allow for the creation of Java-specific Keys and Paths depending on GAV Parameters
	protected abstract IndexArtifact generateJavaArtifactPath(JavaIndexKey key);
}
