package com.spedge.hangar.repo.java.index;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

public class JavaIndexKey extends IndexKey {

	private String group = "";
	private String artifact = "";
	private String version = "";
		
	public JavaIndexKey(String key)
	{
		super(RepositoryType.JAVA, key);
		
		String[] split = key.split(":");
		
		this.group = (split.length > 0)? split[0] : "";
		this.artifact = (split.length > 1)? split[1] : "";
		this.version = (split.length > 2)? split[2] : "";
	}
	
	public JavaIndexKey(String group, String artifact, String version)
	{
		super(RepositoryType.JAVA, group + ":" + artifact + ":" + version);

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
		return super.toString();
	}
}
