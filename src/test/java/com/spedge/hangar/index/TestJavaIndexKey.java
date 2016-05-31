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
		assertEquals("RELEASE_JAVA:things:and:stuff", ik.toString());
		assertEquals("things:and:stuff", ik.toPath());
	}

	@Test
	public void testJavaKeyParse()
	{
		JavaIndexKey ik = new JavaIndexKey(RepositoryType.RELEASE_JAVA, "things");
		assertEquals("RELEASE_JAVA:things", ik.toString());
		assertEquals("things", ik.toPath());
		assertEquals(RepositoryType.RELEASE_JAVA, ik.getType());
		
		ik = new JavaIndexKey(RepositoryType.UNKNOWN, "things:and");
		assertEquals("UNKNOWN:things:and", ik.toString());
		assertEquals("things:and", ik.toPath());
		assertEquals(RepositoryType.UNKNOWN, ik.getType());
		
		ik = new JavaIndexKey(RepositoryType.PROXY_JAVA, "things:and:stuff");
		assertEquals("PROXY_JAVA:things:and:stuff", ik.toString());
		assertEquals("things:and:stuff", ik.toPath());
		assertEquals(RepositoryType.PROXY_JAVA, ik.getType());
	}
}
