package com.spedge.hangar.testutils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.java.JavaIndexArtifact;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorage;
import com.spedge.hangar.storage.IStorageTranslator;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

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
		JavaIndexArtifact ia = new JavaIndexArtifact(key.toString());
		return ia;
	}

	public StreamingOutput getArtifactStream(IndexArtifact key, String filename) {
		return new FakeStreamingOutput(fakeStorage.get(key.getLocation()));
	}

	public void uploadReleaseArtifactStream(IndexArtifact key, StorageRequest sr) throws StorageException {
		fakeStorage.put(key.getLocation(), sr.getFilename());
	}

	public void uploadSnapshotArtifactStream(IndexArtifact key, StorageRequest sr) throws StorageException {
		fakeStorage.put(key.getLocation(), sr.getFilename());
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
	public List<IndexKey> getArtifactKeys(String uploadPath) throws StorageException { return new ArrayList<IndexKey>(); }

	@Override
	public IndexArtifact getIndexArtifact(IndexKey key, String uploadPath) throws IndexException 
	{ 
		IndexArtifact ia = new JavaIndexArtifact("fakestorage");
		return ia;
	}

    @Override
    public void initialiseStorage(IStorageTranslator st, String uploadPath) throws StorageException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public IStorageTranslator getStorageTranslator(String prefixPath)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPath()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
