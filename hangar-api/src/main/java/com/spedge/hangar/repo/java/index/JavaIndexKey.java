package com.spedge.hangar.repo.java.index;

import java.util.Objects;

import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;

public class JavaIndexKey extends IndexKey {

	private String group = "";
	private String artifact = "";
	private String version = "";
		
	public JavaIndexKey(RepositoryType type, String key)
	{
		super(type, key);
		
		String[] split = key.split(":");
		
		this.group = split[0];
		this.artifact = (split.length > 1)? split[1] : "";
		this.version = (split.length > 2)? split[2] : "";
	}
	
	public JavaIndexKey(RepositoryType type, String group, String artifact, String version)
	{
		super(type, group + ":" + artifact + ":" + version);

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
	
	@Override
	public boolean equals(Object o) 
	{
	    // self check
	    if (this == o) return true;
	    // null check
	    if (o == null) return false;
	    // type check and cast
	    if (getClass() != o.getClass()) return false;
	    
	    JavaIndexKey jik = (JavaIndexKey) o;
	    
	    return Objects.equals(toString(), jik.toString()) &&
	    	   Objects.equals(getType(), jik.getType());
	}
}
