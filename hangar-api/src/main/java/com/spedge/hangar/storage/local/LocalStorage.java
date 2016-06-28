package com.spedge.hangar.storage.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang3.StringUtils;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexKey;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaIndexArtifact;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.Storage;
import com.spedge.hangar.storage.StorageException;

public class LocalStorage extends Storage
{
	private HealthCheck check = null;
	
	public HealthCheck getHealthcheck() 
	{
		if(check == null) { check = new LocalStorageHealthcheck(getPath()); }
		return check;
	}
	
	@Override
	public void initialiseStorage(String uploadPath) throws StorageException {
		try 
		{
			Files.createDirectories(Paths.get(getPath() + "/" + uploadPath));
		} 
		catch (IOException e) 
		{
			throw new LocalStorageException();
		}
		
	}

	// From the JavaIndexKey (containing GAV params)
	// generate the path that it should go into on local storage.
	@Override
	protected IndexArtifact generateJavaArtifactPath(JavaIndexKey key, String uploadPath) throws LocalStorageException
	{
		IndexArtifact ia = new JavaIndexArtifact();
		String version = key.getVersion().isEmpty()? "" : "/" + key.getVersion();
		String location = "/" + uploadPath + "/" + key.getGroup().replace('.', '/') + "/" + key.getArtifact() + version;
		ia.setLocation(location);
		
		try
		{
			// Need to generate current state - add which files we have
			Path sourcePath = Paths.get(getPath(), ia.getLocation());
			
			Files.walk(Paths.get(sourcePath.toString()))
			      .filter(Files::isRegularFile)
			      .map(e -> e.toString().replace(sourcePath.toString(), ""))
			      .map(e -> e.substring(e.lastIndexOf(File.separator), e.length()).toString())
			      .forEach(ia::setStoredFile);
		}
		catch (NoSuchFileException nsfe)
		{
			logger.info("[LocalStorage] New Artifact : New Path " + ia.getLocation());
		}
		catch (IOException e) 
		{
			logger.info(e.getMessage());
			throw new LocalStorageException();
		}
		
		return ia;
	}
	
	@Override
	public List<IndexKey> getArtifactKeys(RepositoryType type, String uploadPath) throws LocalStorageException
	{
		try 
		{
			Path sourcePath = Paths.get(getPath(), uploadPath);
			long start = System.currentTimeMillis();
						
			List<IndexKey> paths = Files.walk(Paths.get(sourcePath.toString()))
								        .filter(Files::isRegularFile)
								        .map(e -> e.toString().replace(sourcePath.toString(), ""))
								        .map(e -> e.subSequence(0, e.lastIndexOf(File.separator)).toString())
								        .map(e -> e.substring(1, StringUtils.lastOrdinalIndexOf(e, File.separator, 2)).replace(File.separator, ".") 
								        		                   + ":" + e.substring(StringUtils.lastOrdinalIndexOf(e, File.separator, 2), e.lastIndexOf(File.separator)).replace(File.separator, "")
								        		                   + ":" + e.substring(e.lastIndexOf(File.separator), e.length()).replace(File.separator, ""))
								        .distinct()
								        .map(e -> new IndexKey(type, e))
								        .collect(Collectors.toList());
			
			long end = System.currentTimeMillis();
			logger.info(paths.size() + " Artifacts Indexed under " + sourcePath.toString() + " in " + (end - start)  + "ms");
			return paths;
		} 
		catch (IOException e) 
		{
			logger.info(e.getMessage());
			throw new LocalStorageException();
		}
		catch(Exception iee)
		{
			logger.info(iee.getMessage());
			throw new LocalStorageException();
		}
	}

	@Override
	public StreamingOutput getArtifactStream(final IndexArtifact artifact, final String filename) {
		
		final String artifactPath = getPath() + artifact.getLocation() + "/" + filename;
		
		if(Files.isReadable(Paths.get(artifactPath)))
    	{
			return new StreamingOutput() {
	            
	            public void write(OutputStream os) throws IOException, WebApplicationException 
	            {
	            	FileInputStream fis = new FileInputStream(artifactPath);
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
	public void uploadReleaseArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream) throws LocalStorageException {
		uploadArtifactStream(artifact, filename, uploadedInputStream);
	}

	@Override
	public void uploadSnapshotArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream) throws LocalStorageException {
		uploadArtifactStream(artifact, filename, uploadedInputStream, StandardCopyOption.REPLACE_EXISTING);
	}

	private void uploadArtifactStream(IndexArtifact artifact, String filename, InputStream uploadedInputStream, StandardCopyOption... options) throws LocalStorageException {
		
		Path outputPath = FileSystems.getDefault().getPath(getPath() + artifact.getLocation());
		Path outputPathArtifact = FileSystems.getDefault().getPath(getPath() + artifact.getLocation() + "/" + filename);
		
		try 
		{
			Files.createDirectories(outputPath);
			Files.copy(uploadedInputStream, outputPathArtifact, options);
		} 
		catch (IOException e) 
		{
			logger.error(e.getLocalizedMessage());
			throw new LocalStorageException();
		}
	}
}