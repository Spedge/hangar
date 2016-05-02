package com.spedge.hangar.repo.java;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

public class JavaIndexKey extends IndexKey {

	private static final RepositoryType type = RepositoryType.JAVA;
	private String group;
	private String artifact;
	private String version;
	
	public JavaIndexKey(String group, String artifact)
	{
		this.group = group;
		this.artifact = artifact;
		this.version = "";
	}
	
	public JavaIndexKey(String group, String artifact, String version)
	{
		this.group = group;
		this.artifact = artifact;
		this.version = version;
	}
	
	public String getGroup()
	{
		return group;
	}
	
	public String getArtifact()
	{
		return artifact;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public String toString()
	{
		return type.toString() + ":" + group + ":" + artifact + ":" + version;
	}
}
