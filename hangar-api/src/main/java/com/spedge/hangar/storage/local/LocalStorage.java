package com.spedge.hangar.storage.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.storage.IStorageTranslator;
import com.spedge.hangar.storage.Storage;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

public class LocalStorage extends Storage
{
    private HealthCheck check = null;
    private FileSystem fs = FileSystems.getDefault();

    /**
     * Return the LocalStorage healthcheck.
     */
    public HealthCheck getHealthcheck()
    {
        if (check == null)
        {
            check = new LocalStorageHealthcheck(getPath());
        }
        return check;
    }

    @Override
    public void initialiseStorage(IStorageTranslator st, String uploadPath) throws StorageException
    {
        try
        {
            Path initialDir = fs.getPath(getPath(), uploadPath);
            Files.createDirectories(initialDir);
            addPathTranslator(st, uploadPath);
        }
        catch (IOException ioe)
        {
            throw new LocalStorageException(ioe);
        }
    }

    @Override
    public List<IndexKey> getArtifactKeys(String uploadPath) throws LocalStorageException
    {
        Path sourcePath = fs.getPath(getPath(), uploadPath);
        IStorageTranslator st = getStorageTranslator(uploadPath);
       
        long start = System.currentTimeMillis();
        List<IndexKey> paths = st.getLocalStorageKeys(sourcePath);

        long end = System.currentTimeMillis();
        logger.info(paths.size() + " Artifacts Indexed under " + sourcePath.toString() + " in "
                + (end - start) + "ms");
        return paths;
    }

    @Override
    public StreamingOutput getArtifactStream(final IndexArtifact artifact, final String filename)
    {
        Path streamPath = fs.getPath(getPath() + artifact.getLocation() + "/" + filename);

        if (Files.isReadable(streamPath))
        {
            return new StreamingOutput()
            {

                public void write(OutputStream os) throws IOException, WebApplicationException
                {
                    FileInputStream fis = new FileInputStream(streamPath.toString());
                    ByteStreams.copy(fis, os);
                    fis.close();
                }
            };
        }
        else
        {
            throw new NotFoundException();
        }
    }

    @Override
    public void uploadReleaseArtifactStream(IndexArtifact ia, StorageRequest sr)
            throws LocalStorageException
    {
        uploadArtifactStream(ia, sr);
    }

    @Override
    public void uploadSnapshotArtifactStream(IndexArtifact ia, StorageRequest sr)
            throws LocalStorageException
    {
        uploadArtifactStream(ia, sr, StandardCopyOption.REPLACE_EXISTING);
    }

    private void uploadArtifactStream(IndexArtifact ia, StorageRequest sr,
            StandardCopyOption... options) throws LocalStorageException
    {
        Path outputPath = fs.getPath(getPath() + ia.getLocation());
        Path outputPathArtifact = fs.getPath(getPath() + ia.getLocation() + "/" + sr.getFilename());

        try
        {
            Files.createDirectories(outputPath);
            Files.copy(sr.getNewStream(), outputPathArtifact, options);
        }
        catch (IOException ioe)
        {
            logger.error(ioe.getLocalizedMessage());
            throw new LocalStorageException(ioe);
        }
    }

    // This is used so we can mock the filesystem
    // during unit testing.
    public void setFilesystem(FileSystem fs)
    {
        this.fs = fs;
    }
}
