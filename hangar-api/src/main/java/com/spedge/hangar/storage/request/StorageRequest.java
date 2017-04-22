package com.spedge.hangar.storage.request;

import com.google.common.io.ByteStreams;
import com.spedge.hangar.config.ArtifactLanguage;
import com.spedge.hangar.storage.request.StorageRequest.StorageRequestBuilder;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

public class StorageRequest
{
    private int length;
    private byte[] stream;
    private StorageRequestKey key;
    private boolean isOverwritable = false;
    private ArtifactLanguage language;
    
    /**
     * Creates a StorageRequest to be used to both store and retrieve data from 
     * the Storage Layer.
     * @param srb A StorageRequestBuilder to maintain an immutable object
     */
    public StorageRequest(StorageRequestBuilder srb)
    {
        this.length = srb.length;
        this.stream = srb.stream;
        this.key = new StorageRequestKey(srb.index, srb.filename);
        this.isOverwritable = srb.overwritable;
        this.language = srb.language;
    }

    public int getLength()
    {
        return length;
    }

    public StorageRequestKey getKey()
    {
        return key;
    }
    
    public boolean isOverwritable()
    {
        return this.isOverwritable;
    }
    
    public ArtifactLanguage getLanguage()
    {
        return this.language;
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
    
    public byte[] getStreamAsByteArray()
    {
        return stream;
    }
    
    public static class StorageRequestBuilder 
    {
        private int length;
        private byte[] stream;
        private String filename;
        private boolean overwritable = false;
        private List<String> index;
        private ArtifactLanguage language;
        
        public StorageRequestBuilder(){}
        
        /**
         * To be used when you've got a half filled StorageRequest and you want
         * to add more to it - usually when a request is redirected to a proxy
         * and additional information is added (because the artifact is downloaded).
         * 
         * @param sr Existing StorageRequest
         */
        public StorageRequestBuilder(StorageRequest sr)
        {
            this.length = sr.getLength();
            this.stream = sr.getStreamAsByteArray();
            this.filename = sr.getKey().getFilename();
            this.index = sr.getKey().getKey();
            this.overwritable = sr.isOverwritable();
            this.language = sr.getLanguage();
        }

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
        
        public StorageRequestBuilder index(String... index)
        {
            this.index = Arrays.asList(index);
            return this;
        }
        
        public StorageRequestBuilder index(List<String> index)
        {
            this.index = index;
            return this;
        }
        
        public StorageRequestBuilder language(ArtifactLanguage lang)
        {
            this.language = lang;
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
