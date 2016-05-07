package com.spedge.hangar.repo.java;

import static org.junit.Assert.assertSame;

import java.io.InputStream;

import org.junit.Test;

import com.spedge.hangar.index.IIndex;
import com.spedge.hangar.index.InMemoryIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.testutils.TestStorage;
import com.spedge.hangar.testutils.TestStorage.FakeStreamingOutput;

public class TestJavaSnapshotRepository {
	
	@Test
	public void TestGetSnapshot() throws StorageException
	{
		// Define mock artifact
		String group = "com.spedge.test";
		String webgroup = "com/spedge/test";
		String artifact = "test-artifact";
		String version = "0.1.2.3-SNAPSHOT";
		String filename = "test-artifact-0.1.2.3-20160430.090624-1.jar";
		
		// Add mock storage and index to repo
		JavaSnapshotRepository jsr = new JavaSnapshotRepository();
		
		TestStorage storage = new TestStorage();
		jsr.setStorage(storage);
		
		IIndex index = InMemoryIndex.getInstance();
		jsr.setIndex(index);
	
		// Add our mock artifact
		JavaIndexKey key = new JavaIndexKey(group + ":" + artifact + ":" + version);
		IndexArtifact ia = storage.generateArtifactPath(key);
		index.addArtifact(key, ia);
		
		InputStream uploadedInputStream = null;
		storage.uploadSnapshotArtifactStream(ia, filename, uploadedInputStream);
		
		// See what happens!
		FakeStreamingOutput fso = (FakeStreamingOutput) jsr.getSnapshotArtifact(webgroup, artifact, version, filename);
		assertSame(fso.getFilename(), filename);
	}
}
