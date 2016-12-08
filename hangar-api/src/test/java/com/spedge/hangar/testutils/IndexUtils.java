package com.spedge.hangar.testutils;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaIndexArtifact;
import com.spedge.hangar.repo.java.index.JavaIndexKey;

public enum IndexUtils 
{	
	TEST1(new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.spedge.test", "test-artifact", "0.1.2.3"),
		  generateArtifact("/this/is/a/location/")),
	TEST2(new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.spedge.test", "test-artifact", "0.1.2.6"),
		  generateArtifact("/this/is/another/location/"));
	
	private IndexKey key;
	private IndexArtifact ia;
	
	IndexUtils(IndexKey key, IndexArtifact artifact)
	{
		this.key = key;
		this.ia = artifact;
	}

	public IndexKey getKey() {
		return this.key;
	}

	public IndexArtifact getArtifact() {
		return this.ia;
	}
	
	private static IndexArtifact generateArtifact(String location)
	{
		IndexArtifact ia = new JavaIndexArtifact(location);
		return ia;
	}
}
