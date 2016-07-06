package com.spedge.hangar.repo.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
	private static final long serialVersionUID = -37333974652565977L;
	private Map<String, Boolean> fileTypes;
	private String snapshotVersion = "";
	
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
	
	public String getSnapshotVersion()
	{
		return snapshotVersion;
	}
	
	public void setSnapshotVersion(String snapshotVersion)
	{
		this.snapshotVersion = snapshotVersion;
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
	    
	    JavaIndexArtifact jia = (JavaIndexArtifact) o;
	    
	    return Objects.equals(getLocation(), jia.getLocation()) &&
	    	   Objects.equals(getSnapshotVersion(), jia.getSnapshotVersion()) &&
	    	   Objects.equals(getFileTypes(), jia.getFileTypes());
	}
}
