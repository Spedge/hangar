package com.spedge.hangar.storage;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class StorageRequest
{

    private int length;
    private InputStream stream;
    private String filename;

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public InputStream getStream()
    {
        return stream;
    }

    public void setStream(InputStream stream)
    {
        this.stream = stream;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    /**
     * Close the StorageRequest stream.
     */
    public void closeStream()
    {
        if (stream != null)
        {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * Static method to create a new StorageRequest.
     * 
     * @param filename Name of the file
     * @param uploadedInputStream InputStream containing the contents of the file
     * @param contentLength Length of the stream
     * 
     * @return Newly created StorageRequest
     */
    public static StorageRequest create(String filename, InputStream uploadedInputStream,
            int contentLength)
    {
        StorageRequest sr = new StorageRequest();
        sr.setFilename(filename);
        sr.setLength(contentLength);
        sr.setStream(uploadedInputStream);

        return sr;
    }

}
