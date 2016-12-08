package com.spedge.hangar.index.zookeeper;

import static org.junit.Assert.assertEquals;

import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.ReservedArtifact;
import com.spedge.hangar.testutils.IndexUtils;

public class TestZookeeperIndex 
{
	ZooKeeperIndex index;
	
	@Before
	public void setupIndex() throws Exception
	{
		index = new ZooKeeperIndex();
	}
	
	@Test
	public void testAddArtifact() throws Exception
	{
		TestingServer testingServer = new TestingServer();
		index.startClient(testingServer.getConnectString());
		
		try
		{
			// Add an artifact.
			index.addArtifact(IndexUtils.TEST1.getKey(), IndexUtils.TEST1.getArtifact());
			
			// Is there an artifact?
			Assert.assertTrue(index.isArtifact(IndexUtils.TEST1.getKey()));
			
			// Get the artifact.
			assertEquals(IndexUtils.TEST1.getArtifact(), index.getArtifact(IndexUtils.TEST1.getKey()));
		}
		finally
		{
			CloseableUtils.closeQuietly(testingServer);
		}
	}
	
	
	@Test
	public void testReservedArtifact() throws Exception
	{		
		TestingServer testingServer = new TestingServer();
		index.startClient(testingServer.getConnectString());
		
		try
		{
			// Add a reserved artifact
			ReservedArtifact ra = index.addReservationKey(IndexUtils.TEST2.getKey());
					
			// Is there an artifact?
			Assert.assertTrue(index.isArtifact(IndexUtils.TEST2.getKey()));
			
			try
			{
				index.addArtifact(IndexUtils.TEST2.getKey(), IndexUtils.TEST2.getArtifact());
				Assert.fail("Index Conflict was never triggered.");
			}
			catch(IndexConfictException ice){}
			
			// Attempt to update a reserved artifact with an incorrect reserved token!
			try
			{
				index.addReservedArtifact(IndexUtils.TEST2.getKey(), new ReservedArtifact(""), IndexUtils.TEST2.getArtifact());
				Assert.fail("Reserved Conflict was never triggered.");
			}
			catch(IndexConfictException ice){}
			
			// Update the reserved artifact
			index.addReservedArtifact(IndexUtils.TEST2.getKey(), ra, IndexUtils.TEST2.getArtifact());
			
			// Get the artifact.
			assertEquals(IndexUtils.TEST2.getArtifact(), index.getArtifact(IndexUtils.TEST2.getKey()));
			
			// Catch no error when attempting to override it
			index.addArtifact(IndexUtils.TEST2.getKey(), IndexUtils.TEST1.getArtifact());
			
			// Is there an artifact?
			Assert.assertTrue(index.isArtifact(IndexUtils.TEST2.getKey()));
			
			// Get the artifact.
			assertEquals(IndexUtils.TEST1.getArtifact(), index.getArtifact(IndexUtils.TEST2.getKey()));
		}
		finally
		{
			CloseableUtils.closeQuietly(testingServer);
		}
	}
}
