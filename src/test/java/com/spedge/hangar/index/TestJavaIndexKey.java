package com.spedge.hangar.index;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.spedge.hangar.repo.java.index.JavaIndexKey;

public class TestJavaIndexKey {
	
	@Test
	public void testJavaKeyGav()
	{
		JavaIndexKey ik = new JavaIndexKey("things", "and", "stuff");
		assertEquals("JAVA:things:and:stuff", ik.toString());
		assertEquals("things:and:stuff", ik.toPath());
	}

	@Test
	public void testJavaKeyParse()
	{
		JavaIndexKey ik = new JavaIndexKey("things");
		assertEquals("JAVA:things", ik.toString());
		assertEquals("things", ik.toPath());
		
		ik = new JavaIndexKey("things:and");
		assertEquals("JAVA:things:and", ik.toString());
		assertEquals("things:and", ik.toPath());
		
		ik = new JavaIndexKey("things:and:stuff");
		assertEquals("JAVA:things:and:stuff", ik.toString());
		assertEquals("things:and:stuff", ik.toPath());
	}
}
