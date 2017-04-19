package com.spedge.hangar.storage.request;

import com.google.common.io.ByteStreams;
import com.spedge.hangar.config.ArtifactLanguage;

import org.apache.commons.io.IOUtils;

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
    private boolean isOverwritable = false;
    private StorageRequestKey index;
    
    /**
     * Creates a StorageRequest to be used to both store and retrieve data from 
     * the Storage Layer.
     * @param srb A StorageRequestBuilder to maintain an immutable object
     */
    public StorageRequest(StorageRequestBuilder srb)
    {
        this.length = srb.length;
        this.stream = srb.stream;
        this.filename = srb.filename;
        this.isOverwritable = srb.overwritable;
        this.index = srb.index;
    }

    public int getLength()
    {
        return length;
    }

    public String getFilename()
    {
        return filename;
    }
    
    public boolean isOverwritable()
    {
        return this.isOverwritable;
    }
    
    public StorageRequestKey getIndex()
    {
        return index;
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
    
    public static class StorageRequestBuilder 
    {
        private int length;
        private byte[] stream;
        private String filename;
        private boolean overwritable = false;
        private StorageRequestKey index;
        
        public StorageRequestBuilder length(int length)
        {
            this.length = length;
            return this;
        }

        public StorageRequestBuilder stream(byte[] byteArray)
        {
            this.stream = byteArray;
            return this;
        }
        
        public StorageRequestBuilder stream(InputStream uploadedInputStream) throws IOException
        {
            this.stream = IOUtils.toByteArray(uploadedInputStream);
            return this;
        }
        
        public StorageRequestBuilder filename(String filename)
        {
            this.filename = filename;
            return this;
        }
        
        public StorageRequestBuilder overwritable(boolean overwritable)
        {
            this.overwritable = overwritable;
            return this;            
        }
        
        public StorageRequestBuilder index(ArtifactLanguage language, String delimiter, String key)
        {
            this.index = new StorageRequestKey(language, delimiter, key);
            return this;
        }
        
        public StorageRequest build() 
        {
            return new StorageRequest(this);
        }
    }
    
    @Override
    public String toString()
    {
        return new String(stream);
    }
}
