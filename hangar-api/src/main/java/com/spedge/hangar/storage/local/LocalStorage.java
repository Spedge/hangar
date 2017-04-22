package com.spedge.hangar.storage.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.config.ArtifactLanguage;
import com.spedge.hangar.storage.Storage;
import com.spedge.hangar.storage.StorageConfiguration;
import com.spedge.hangar.storage.StorageInitalisationException;
import com.spedge.hangar.storage.request.StorageRequest;
import com.spedge.hangar.storage.request.StorageRequestException;
import com.spedge.hangar.storage.request.StorageRequestKey;

public class LocalStorage extends Storage
{
    private HealthCheck check = null;
    private FileSystem fs = FileSystems.getDefault();
    
    /**
     * prefixPath is the initial absolute path configured at start-up.
     * e.g. /data/maven/
     */
    private String rootPath;
    
    /*
     * We want to make sure the root path is there and accessible so that we can save files to it.
     * 
     * (non-Javadoc)
     * @see com.spedge.hangar.storage.IStorage#initialiseStorage(com.spedge.hangar.storage.StorageConfiguration)
     */
    @Override
    public void initialiseStorage() throws StorageInitalisationException
    {
        StorageConfiguration sc = super.getStorageConfiguration();
        
        // End the initialised path with a trailing slash if it does not already have one.
        this.rootPath = sc.getRootPath().endsWith("/") ? sc.getRootPath() : sc.getRootPath() + "/";
        
        // Initialise the Healthcheck
        this.check = new LocalStorageHealthcheck(this.rootPath);
        
        // Let's try and create the root directory where these items will go.
        try
        {
            Path initialDir = fs.getPath(this.rootPath);
            Files.createDirectories(initialDir);
        }
        catch (IOException ioe)
        {
            throw new StorageInitalisationException(ioe);
        }
         
        super.initialisationComplete();
    }
    
    /*
     * Basic Healthcheck
     * 
     * (non-Javadoc)
     * @see com.spedge.hangar.storage.IStorage#getHealthcheck()
     */
    @Override
    public HealthCheck getHealthcheck()
    {
        return check;
    }
    
    /*
     * We want to use the StorageRequestIndex and convert it into something we can understand.
     * Once it's been made into a path, we attempt to read from it - throwing an error if we can't.
     * 
     * (non-Javadoc)
     * @see com.spedge.hangar.storage.IStorage#getArtifactStream(com.spedge.hangar.storage.StorageRequest)
     */
    @Override
    public StreamingOutput getArtifactStream(StorageRequest sr) throws StorageRequestException
    {                       
        // Determine the location of this artifact on storage according to the index.
        Path streamPath = this.convertIndex(sr);
        
        // Check if we can read it - if so, return a stream to the source.
        if (Files.isReadable(streamPath))
        {
            return new StreamingOutput()
            {
                public void write(OutputStream os) throws IOException
                {
                    FileInputStream fis = new FileInputStream(streamPath.toString());
                    ByteStreams.copy(fis, os);
                    fis.close();
                }
            };
        }
        else
        {
            // If it's not readable, we should throw an exception describing why.
            throw new StorageRequestException(StorageRequestException.DOES_NOT_EXIST);
        }
    }

    @Override
    public void uploadArtifactStream(StorageRequest sr) throws StorageRequestException
    {
        // Determine the location of this artifact on storage according to the index.
        Path streamPath = this.convertIndex(sr);
        
        // If this is considered by the API to be an artifact that can be overwritten, allow it.
        StandardCopyOption[] options = sr.isOverwritable() 
                        ? new StandardCopyOption[]{StandardCopyOption.REPLACE_EXISTING} 
                        : new StandardCopyOption[]{};

        try
        {
            // Copy the contents of the stream into the location.
            Files.createDirectories(streamPath.getParent());
            Files.copy(sr.getNewStream(), streamPath, options);
        }
        catch (IOException ioe)
        {
            // This is an unexpected error condition - throw an exception.
            logger.error(ioe.getLocalizedMessage());
            throw new StorageRequestException(ioe);
        }
    }
    
    @Override
    public List<StorageRequestKey> getArtifactKeys(StorageRequest sr) throws StorageRequestException
    {
        List<StorageRequestKey> keys;
        Path sourcePath = convertIndex(sr);
        
        try
        {
            keys = Files.walk(sourcePath)
                        .filter(Files::isRegularFile) // For each file
                        .map(e -> e.toString().replace(sourcePath.toString(), "")) // Remove the absolute path
                        .distinct() // Get rid of any replicas
                        .map(e -> fs.getPath(e)) // Rebuild into a path
                        .map(e -> new StorageRequestKey(new ArrayList<String>(Arrays.asList(e.getParent().toString().split("/"))), e.getFileName().toString()))
                        .collect(Collectors.toList());
            
            return keys;
        }
        catch (IOException ioe)
        {
            throw new StorageRequestException(ioe);
        }
    }
    
    /**
     * Converts a StorageRequestIndex entry into a Path to be used by the LocalStorage layer
     * 
     * @param index An entry containing data that we can create a path from.
     * @return A full path to the location stated in the StorageRequestIndex.
     */
    private Path convertIndex(StorageRequest sr)
    {       
        return fs.getPath(this.rootPath, sr.getKey().getFullKey());
    }
    
    @Override
    public void removeArtifact(StorageRequest sr) throws StorageRequestException
    {
        // TODO Not sure how we'll do this in the local storage.
        // I'm tempted to create a /old directory and simply do a move,
        // then expect that a garbage collection process will remove these when
        // it's an acceptable time.
    }
}
