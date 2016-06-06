package com.spedge.hangar.repo.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
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
	
	private JavaSnapshotRepository jsr;
	private TestStorage storage;
	private InMemoryIndex index;
	
	@Before
	public void prepareSnapshotRepo() throws StorageException
	{
		// Add mock storage and index to repo
		jsr = new JavaSnapshotRepository();
		
		StorageConfiguration sc = new StorageConfiguration();
		sc.setUploadPath("test-path");
		
		storage = new TestStorage();
		jsr.setStorageConfiguration(sc);
		index = new InMemoryIndex();
		
		HangarConfiguration hc = new HangarConfiguration();
		hc.setStorage(storage);
		hc.setIndex(index);

		jsr.loadRepository(hc, null);
	}
	
	@Test
	public void TestGetSnapshot() throws StorageException
	{
		// Define mock artifact
		String group = "com.spedge.test";
		String webgroup = "com/spedge/test";
		String artifact = "test-artifact";
		String version = "0.1.2.3-SNAPSHOT";
		String filename = "test-artifact-0.1.2.3-20160430.090624-1.jar";
					
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
	
	@Test
	public void TestPutNewSnapshot() throws StorageException
	{
		// Define mock artifact
		String webgroup = "com/spedge/test";
		String artifact = "test-artifact";
		String version = "0.1.2.4-SNAPSHOT";
		String filename = "test-artifact-0.1.2.4-20160430.090624-1.jar";
		String fileContent = "thisisajarwoo";
		InputStream uploadedInputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
		
		Response rep = jsr.uploadArtifact(webgroup, artifact, version, filename, uploadedInputStream);
		assertEquals(rep.getStatus(), HttpStatus.OK_200);
		
		// See what happens!
		FakeStreamingOutput fso = (FakeStreamingOutput) jsr.getSnapshotArtifact(webgroup, artifact, version, filename);
		assertSame(fso.getFilename(), filename);
	}
}
