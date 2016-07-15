package com.spedge.hangar.storage;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class StorageRequest {
	
	private int length;
	private InputStream stream;
	private String filename;
	
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public InputStream getStream() {
		return stream;
	}
	public void setStream(InputStream stream) {
		this.stream = stream;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void closeStream() {
		if(stream != null) { IOUtils.closeQuietly(stream); }
	}
	public static StorageRequest create(String filename, InputStream uploadedInputStream, int contentLength) 
	{
		StorageRequest sr = new StorageRequest();
		sr.setFilename(filename);
		sr.setLength(contentLength);
		sr.setStream(uploadedInputStream);
		
		return sr;
	}
	
	

}
