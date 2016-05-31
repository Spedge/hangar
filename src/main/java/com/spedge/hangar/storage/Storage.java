package com.spedge.hangar.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryLanguage;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.local.LocalStorageException;

public abstract class Storage implements IStorage
{
	protected final static Logger logger = LoggerFactory.getLogger(Storage.class);
		
	@Override
	public IndexArtifact generateArtifactPath(RepositoryType type, String uploadPath, IndexKey key) throws LocalStorageException 
	{
		IndexArtifact ia;
		
		if(type.getLanguage().equals(RepositoryLanguage.JAVA)) { ia = generateJavaArtifactPath(new JavaIndexKey(type, key.toPath()), uploadPath); }
		else throw new LocalStorageException();
		
		return ia;
	}
	
	// These two methods allow for the creation of Java-specific Keys and Paths depending on GAV Parameters
	protected abstract IndexArtifact generateJavaArtifactPath(JavaIndexKey key, String uploadPath) throws LocalStorageException;
}
