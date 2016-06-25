package com.spedge.hangar.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaIndexArtifact;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.StorageException;

public class TestStorage implements IStorage 
{
	private Map<String, String> fakeStorage;
	
	public TestStorage()
	{
		fakeStorage = new HashMap<String, String>();
	}
	
	public HealthCheck getHealthcheck() {
		return null;
	}

	public IndexArtifact generateArtifactPath(JavaIndexKey key) {
		JavaIndexArtifact ia = new JavaIndexArtifact();
		ia.setLocation(key.toString());
		return ia;
	}

	public StreamingOutput getArtifactStream(IndexArtifact key, String filename) {
		return new FakeStreamingOutput(fakeStorage.get(key.getLocation()));
	}

	public void uploadReleaseArtifactStream(IndexArtifact key, String filename, InputStream uploadedInputStream) throws StorageException {
		fakeStorage.put(key.getLocation(), filename);
	}

	public void uploadSnapshotArtifactStream(IndexArtifact key, String filename, InputStream uploadedInputStream) throws StorageException {
		fakeStorage.put(key.getLocation(), filename);
	}
	
	public class FakeStreamingOutput implements StreamingOutput
	{
		private String filename;
		
		FakeStreamingOutput(String filename)
		{
			this.filename = filename;
		}
		public String getFilename() { return filename; }
		public void write(OutputStream arg0) throws IOException, WebApplicationException {}
	}

	@Override
	public List<IndexKey> getArtifactKeys(RepositoryType type, String uploadPath) throws StorageException { return new ArrayList<IndexKey>(); }

	@Override
	public IndexArtifact generateArtifactPath(RepositoryType type, String uploadPath, IndexKey key) throws StorageException 
	{ 
		IndexArtifact ia = new JavaIndexArtifact();
		ia.setLocation("fakestorage");
		return ia;
	}

	@Override
	public void initialiseStorage(String uploadPath) throws StorageException {
		// TODO Auto-generated method stub
		
	}
}
