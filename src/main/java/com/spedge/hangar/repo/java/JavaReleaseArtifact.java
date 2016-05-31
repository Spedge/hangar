package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;

import com.spedge.hangar.index.IndexArtifact;

public class JavaReleaseArtifact extends IndexArtifact
{
	private Map<String, Boolean> fileTypes;
	
	public JavaReleaseArtifact()
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
