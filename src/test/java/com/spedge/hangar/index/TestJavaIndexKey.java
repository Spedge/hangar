package com.spedge.hangar.index;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;

public class TestJavaIndexKey {
	
	@Test
	public void testJavaKeyGav()
	{
		JavaIndexKey ik = new JavaIndexKey(RepositoryType.RELEASE_JAVA, "things", "and", "stuff");
		assertEquals("JAVA:things:and:stuff", ik.toString());
		assertEquals("things:and:stuff", ik.toPath());
		assertEquals("things", ik.getGroup());
		assertEquals("and", ik.getArtifact());
		assertEquals("stuff", ik.getVersion());
		assertEquals(RepositoryType.RELEASE_JAVA, ik.getType());
	}

	@Test
	public void testJavaKeyParse()
	{
		JavaIndexKey ik = new JavaIndexKey(RepositoryType.RELEASE_JAVA, "");
		assertEquals("JAVA:", ik.toString());
		assertEquals("", ik.toPath());
		assertEquals("", ik.getGroup());
		assertEquals("", ik.getArtifact());
		assertEquals("", ik.getVersion());
		assertEquals(RepositoryType.RELEASE_JAVA, ik.getType());
		
		ik = new JavaIndexKey(RepositoryType.RELEASE_JAVA, "things");
		assertEquals("JAVA:things", ik.toString());
		assertEquals("things", ik.toPath());
		assertEquals("things", ik.getGroup());
		assertEquals("", ik.getArtifact());
		assertEquals("", ik.getVersion());
		assertEquals(RepositoryType.RELEASE_JAVA, ik.getType());
		
		ik = new JavaIndexKey(RepositoryType.UNKNOWN, "things:and");
		assertEquals("UNKNOWN:things:and", ik.toString());
		assertEquals("things:and", ik.toPath());
		assertEquals("things", ik.getGroup());
		assertEquals("and", ik.getArtifact());
		assertEquals("", ik.getVersion());
		assertEquals(RepositoryType.UNKNOWN, ik.getType());
		
		ik = new JavaIndexKey(RepositoryType.PROXY_JAVA, "things:and:stuff");
		assertEquals("JAVA:things:and:stuff", ik.toString());
		assertEquals("things:and:stuff", ik.toPath());
		assertEquals("things", ik.getGroup());
		assertEquals("and", ik.getArtifact());
		assertEquals("stuff", ik.getVersion());
		assertEquals(RepositoryType.PROXY_JAVA, ik.getType());
	}
}
