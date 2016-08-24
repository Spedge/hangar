package com.spedge.hangar.index;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.testutils.EqualsTester;

public class TestIndexKey {
	
	@Test
	public void testIndexKey()
	{
		IndexKey ik = new IndexKey(RepositoryType.UNKNOWN, "thingsandstuff");
		assertEquals("UNKNOWN:thingsandstuff", ik.toString());
		assertEquals("thingsandstuff", ik.toPath());
		assertEquals("UNKNOWN", ik.getType().toString());
	}
	
    @Test
    public void testEqualsAndHashCode() 
    {
        EqualsTester<IndexKey> equalsTester = EqualsTester.newInstance(new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:thing:1.0.0"));
        
        equalsTester.assertEqual( new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:thing:1.0.0"), new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:thing:1.0.0"));
        equalsTester.assertEqual( new IndexKey(RepositoryType.SNAPSHOT_JAVA, "com.test:thing:1.0.0"), new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:thing:1.0.0"));
        
        equalsTester.assertNotEqual( new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:test-thing:1.0.0"), new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:thing:1.0.0"));
        equalsTester.assertNotEqual( new IndexKey(RepositoryType.PROXY_PYTHON, "com.test:test-thing:1.0.0"), new IndexKey(RepositoryType.RELEASE_JAVA, "com.test:test-thing:1.0.0") );
    }
}
