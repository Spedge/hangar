package com.spedge.hangar.repo.java;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class TestJavaIndexArtifact {

	@Test
	public void testFileTypes()
	{
		JavaIndexArtifact jia = new JavaIndexArtifact("");
		
		Map<String, Boolean> types = jia.getFileTypes();
		assertFalse(types.get("jar"));
		assertFalse(types.get("jar.sha1"));
		assertFalse(types.get("jar.md5"));
		assertFalse(types.get("pom"));
		assertFalse(types.get("pom.sha1"));
		assertFalse(types.get("pom.md5"));
	}
	
	@Test
	public void testStored()
	{
		JavaIndexArtifact jia = new JavaIndexArtifact("");

		jia.setStoredFile("jar");
		assertTrue(jia.isStoredFile("jar"));
		
		jia.setStoredFile("xml");
		assertFalse(jia.isStoredFile("xml"));
	}
}
