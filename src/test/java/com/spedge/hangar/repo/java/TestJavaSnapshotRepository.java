package com.spedge.hangar.repo.java;

import static org.junit.Assert.assertSame;

import java.io.InputStream;

import org.junit.Test;

import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.InMemoryIndex;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageConfiguration;
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
		
		StorageConfiguration sc = new StorageConfiguration();
		sc.setUploadPath("test-path");
		
		TestStorage storage = new TestStorage();
		jsr.setStorageConfiguration(sc);
		InMemoryIndex index = new InMemoryIndex();
		
		HangarConfiguration hc = new HangarConfiguration();
		hc.setStorage(storage);
		hc.setIndex(index);

		jsr.loadRepository(hc, null);
			
		// Add our mock artifact
		JavaIndexKey key = new JavaIndexKey(RepositoryType.SNAPSHOT_JAVA, group + ":" + artifact + ":" + version);
		IndexArtifact ia = storage.generateArtifactPath(key);
		index.addArtifact(key, ia);
		
		InputStream uploadedInputStream = null;
		storage.uploadSnapshotArtifactStream(ia, filename, uploadedInputStream);
		
		// See what happens!
		FakeStreamingOutput fso = (FakeStreamingOutput) jsr.getSnapshotArtifact(webgroup, artifact, version, filename);
		assertSame(fso.getFilename(), filename);
	}
}