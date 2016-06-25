package com.spedge.hangar.index;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.spedge.hangar.repo.RepositoryType;

public class TestIndexKey {
	
	@Test
	public void testIndexKey()
	{
		IndexKey ik = new IndexKey(RepositoryType.UNKNOWN, "thingsandstuff");
		assertEquals("UNKNOWN:thingsandstuff", ik.toString());
		assertEquals("thingsandstuff", ik.toPath());
	}
}
