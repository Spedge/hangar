package com.spedge.hangar.storage;

import org.apache.commons.io.IOUtils;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class StorageRequest
{
    private int length;
    private byte[] stream;
    private String filename;

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public void setStream(byte[] byteArray)
    {
        this.stream = byteArray;
    }
    
    public void setStream(InputStream uploadedInputStream) throws IOException
    {
        this.setStream(IOUtils.toByteArray(uploadedInputStream));
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
     * Static method to create a new StorageRequest.
     * 
     * @param filename Name of the file
     * @param input InputStream containing the contents of the file
     * @param contentLength Length of the stream
     * 
     * @return Newly created StorageRequest
     * @throws IOException during creation of stream byte-array.
     */
    public static StorageRequest create(String filename, InputStream input, int contentLength) throws IOException
    {
        StorageRequest sr = new StorageRequest();
        sr.setFilename(filename);
        sr.setLength(contentLength);
        sr.setStream(IOUtils.toByteArray(input));

        return sr;
    }
    
    /**
     * Static method to create a new StorageRequest.
     * 
     * @param filename Name of the file
     * @param input InputStream containing the contents of the file
     * @param contentLength Length of the stream
     * 
     * @return Newly created StorageRequest
     * @throws IOException during creation of stream byte-array.
     */
    public static StorageRequest create(String filename, byte[] input, int contentLength) throws IOException
    {
        StorageRequest sr = new StorageRequest();
        sr.setFilename(filename);
        sr.setLength(contentLength);
        sr.setStream(input);

        return sr;
    }
    
    /**
     * Creates a StreamingOutput to be returned as part of a Web Request.
     * @return A StreamingOutput 
     */
    public StreamingOutput getStreamingOutput()
    {
        final InputStream writer = this.getNewStream();
        return new StreamingOutput()
        {
    
            public void write(OutputStream os)
                    throws IOException, WebApplicationException
            {
                ByteStreams.copy(writer, os);
                writer.close();
            }
        };
    }

    public InputStream getNewStream()
    {
        return new ByteArrayInputStream(stream);
    }



}
