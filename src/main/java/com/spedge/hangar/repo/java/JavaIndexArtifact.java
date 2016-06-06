package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.index.IndexArtifact;

/**
 * This class details the files that you should expect as part
 * of a Java Artifact. These are based around a jar and a pom, 
 * with md5s or sha1s of both.
 * 
 * The files will be turned from false to true by the storage
 * layer during indexing - allowing the system to determine
 * what is available without doing a call to storage.
 * 
 * @author Spedge
 *
 */
public class JavaIndexArtifact extends IndexArtifact
{
	private Map<String, Boolean> fileTypes;
	
	public JavaIndexArtifact()
	{
		fileTypes = new HashMap<String, Boolean>();
		fileTypes.put("jar", false);
		fileTypes.put("jar.sha1", false);
		fileTypes.put("jar.md5", false);
		fileTypes.put("pom", false);
		fileTypes.put("pom.md5", false);
		fileTypes.put("pom.sha1", false);
	}
	
	@Override
	protected Map<String, Boolean> getFileTypes() 
	{
		return fileTypes;
	}
}
