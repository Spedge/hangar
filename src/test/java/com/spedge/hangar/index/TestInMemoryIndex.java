package com.spedge.hangar.index;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.spedge.hangar.index.memory.InMemoryIndex;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.testutils.TestStorage;

public class TestInMemoryIndex 
{
	InMemoryIndex index;
	IStorage storage;
	
	@Before
	public void setupIndex()
	{
		index = new InMemoryIndex();
		storage = new TestStorage();
	}
	
	@Test
	public void TestReservedArtifact() throws StorageException, IndexConfictException
	{		
		// Define mock artifact
		String group = "com.spedge.test";
		String webgroup = "com/spedge/test";
		String artifact = "test-artifact";
		String version = "0.1.2.3";
		String filename = "test-artifact-0.1.2.3.jar";
					
		// Add our mock artifact
		JavaIndexKey key = new JavaIndexKey(RepositoryType.RELEASE_JAVA, group + ":" + artifact + ":" + version);
		
		// Add a reserved artifact
		ReservedArtifact ra = index.addReservationKey(key);
				
		// Catch the failed attempt to override it
		IndexArtifact ia = storage.generateArtifactPath(RepositoryType.RELEASE_JAVA, "here", key);	
		
		try
		{
			index.addArtifact(key, ia);
			Assert.fail("Index Conflict was never triggered.");
		}
		catch(IndexConfictException ice){}
		
		// Attempt to update a reserved artifact with an incorrect reserved token!
		try
		{
			index.addReservedArtifact(key, new ReservedArtifact(), ia);
			Assert.fail("Reserved Conflict was never triggered.");
		}
		catch(IndexConfictException ice){}
		
		// Update the reserved artifact
		index.addReservedArtifact(key, ra, ia);
		
		// Catch no error to override it
		index.addArtifact(key, ia);
	}
}
