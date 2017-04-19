package com.spedge.hangar.storage;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.spedge.hangar.storage.request.StorageRequest;

public class TestStorageRequest
{
    @Test
    public void testBuilder() throws IOException
    {
        byte[] storageArray = "Any String you want".getBytes();
        
        StorageRequest sr = new StorageRequest.StorageRequestBuilder()
                            .stream(storageArray)
                            .filename("A Filename")
                            .length(42)
                            .build();
        
        assertEquals("A Filename", sr.getFilename());
        assertEquals(42, sr.getLength());
        assertEquals("Any String you want", IOUtils.toString(sr.getNewStream()));
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sr.getStreamingOutput().write(outputStream);
        
        assertEquals("Any String you want", outputStream.toString());
    }
}
