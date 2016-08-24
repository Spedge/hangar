package com.spedge.hangar.repo.java.index;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.testutils.EqualsTester;

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
    public void testEqualsAndHashCode() 
    {
        EqualsTester<JavaIndexKey> equalsTester = EqualsTester.newInstance(new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "thing", "1.0.0"));
        
        equalsTester.assertEqual( new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "thing", "1.0.0") , new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "thing", "1.0.0") );
        equalsTester.assertEqual( new JavaIndexKey(RepositoryType.SNAPSHOT_JAVA, "com.test", "thing", "1.0.0") , new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "thing", "1.0.0") );
        
        equalsTester.assertNotEqual( new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "test-thing", "1.0.0"), new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "thing", "1.0.0") );
        equalsTester.assertNotEqual( new JavaIndexKey(RepositoryType.PROXY_PYTHON, "com.test", "test-thing", "1.0.0"), new JavaIndexKey(RepositoryType.RELEASE_JAVA, "com.test", "test-thing", "1.0.0") );
    }
}
