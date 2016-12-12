package com.spedge.hangar.repo.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.apache.commons.io.input.ReaderInputStream;
import org.easymock.EasyMock;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.spedge.hangar.config.HangarConfiguration;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.memory.InMemoryIndex;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.api.JavaSnapshotEndpoint;
import com.spedge.hangar.repo.java.base.JavaGroup;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;
import com.spedge.hangar.testutils.TestStorage;
import com.spedge.hangar.testutils.TestStorage.FakeStreamingOutput;

public class TestJavaSnapshotRepository {
	
	private JavaSnapshotEndpoint jsr;
	private TestStorage storage;
	private InMemoryIndex index;
	
	@Before
	public void prepareSnapshotRepo() throws StorageException, IndexException
	{
		// Add mock storage and index to repo
		jsr = new JavaSnapshotEndpoint();
		
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
	public void TestGetSnapshot() throws StorageException, IndexConfictException, IOException
	{
		// Define mock artifact
		String group = "com.spedge.test";
		String webgroup = "com/spedge/test";
		String artifact = "test-artifact";
		String version = "0.1.2.3-SNAPSHOT";
		String filename = "test-artifact-0.1.2.3-20160430.090624-1.jar";
					
		// Add our mock artifact
		JavaIndexKey key = new JavaIndexKey(RepositoryType.SNAPSHOT_JAVA, JavaGroup.slashDelimited(webgroup), artifact, version);
		IndexArtifact ia = storage.generateArtifactPath(key);
		index.addArtifact(key, ia);
		
		byte[] storageArray = "Any String you want".getBytes();
		
        StorageRequest sr = new StorageRequest.StorageRequestBuilder()
                                              .length(0)
                                              .stream(storageArray)
                                              .filename(filename)
                                              .build();
        
		storage.uploadSnapshotArtifactStream(ia, sr);
		
		// See what happens!
		FakeStreamingOutput fso = (FakeStreamingOutput) jsr.getSnapshotArtifact(webgroup, artifact, version, filename);
		assertSame(fso.getFilename(), filename);
	}
	
	@Test
	public void TestAddNextSnapshot() throws StorageException, IOException
	{
		// Define the mock artifact that currently exists.
		// Define mock artifact
		String webgroup = "com/spedge/test";
		String artifact = "test-artifact";
		String version = "0.1.2.4-SNAPSHOT";
		String filename = "test-artifact-0.1.2.4-SNAPSHOT.jar";
		String fileContent = "thisisajarwoo";
		InputStream uploadedInputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
		
		// Upload it to the system
		HttpServletRequest req = EasyMock.createMock(HttpServletRequest.class);
		Response rep = jsr.uploadArtifact(req, webgroup, artifact, version, filename, uploadedInputStream);
		assertEquals(rep.getStatus(), HttpStatus.OK_200);
			
		// Begin the registration of a new artifact.
		fileContent = "thisisajarwoo2";
		
		uploadedInputStream = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
		
		// Upload it to the system
		HttpServletRequest resp = EasyMock.createMock(HttpServletRequest.class);
		rep = jsr.uploadArtifact(resp, webgroup, artifact, version, filename, uploadedInputStream);
		assertEquals(rep.getStatus(), HttpStatus.OK_200);
		uploadedInputStream.close();
		
		// This should fail as we've not uploaded the metadata at this point.
		try
		{
			jsr.getSnapshotArtifact(webgroup, artifact, version, filename);
			Assert.fail();
		}
		catch(NotFoundException nfe){}
		
		String metadata = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><metadata modelVersion=\"1.1.0\"><versioning>" +
		    "<snapshot><timestamp>20160612.173559</timestamp></snapshot></versioning></metadata>";
		
		StringReader reader = new StringReader(metadata);
		uploadedInputStream = new ReaderInputStream(reader);
		uploadedInputStream.close();
		
		// TODO : I don't know why streams don't work in a test, but they do IRL.
		// Will investigate.
		
//		rep = jsr.uploadMetadata(webgroup, artifact, version, "", uploadedInputStream);
//		
//		FakeStreamingOutput fso = (FakeStreamingOutput) jsr.getSnapshotArtifact(webgroup, artifact, version, filename);
//		assertSame(fso.getFilename(), filename);
	}
}
