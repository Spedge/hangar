package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.index.IndexArtifact;

public class JavaIndexArtifact extends IndexArtifact
{
	private Map<String, Boolean> fileTypes;
	
	public JavaIndexArtifact()
	{
		fileTypes = new HashMap<String, Boolean>();
		fileTypes.put("jar", true);
		fileTypes.put("jar.sha1", true);
		fileTypes.put("jar.md5", true);
		fileTypes.put("pom", true);
		fileTypes.put("pom.md5", true);
		fileTypes.put("pom.sha1", true);
	}
	
	@Override
	protected Map<String, Boolean> getFileTypes() 
	{
		return fileTypes;
	}
}
