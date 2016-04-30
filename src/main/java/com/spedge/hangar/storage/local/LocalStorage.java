package com.spedge.hangar.storage.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.hibernate.validator.constraints.NotEmpty;

import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.io.ByteStreams;
import com.spedge.hangar.storage.Storage;

public class LocalStorage extends Storage
{
	private HealthCheck check = null;
	
	@NotEmpty
	private String path;
	
	@NotEmpty
	private String size;

	public HealthCheck getHealthcheck() 
	{
		if(check == null) { check = new LocalStorageHealthcheck(path, size); }
		return check;
	}

	@JsonProperty
	public String getPath() {
		return path;
	}

	@JsonProperty
	public void setPath(String path) {
		this.path = path;
	}

	@JsonProperty
	public String getSize() {
		return size;
	}

	@JsonProperty
	public void setSize(String size) {
		this.size = size;
	}

	public StreamingOutput getArtifactStream(final String artifact_path, final String artifact) {
		
		return new StreamingOutput() {
            
            public void write(OutputStream os) throws IOException, WebApplicationException 
            {
            	if(Files.isReadable(Paths.get(path + "/" + artifact_path + "/" + artifact)))
            	{
            		ByteStreams.copy(new FileInputStream(path + "/" + artifact_path + "/" + artifact), os);
            	}
            	else
            	{
            		throw new NotFoundException();
            	}
            }
        };
	}

	private Response uploadArtifactStream(String artifact_path, String artifact, InputStream uploadedInputStream, StandardCopyOption... options) {
		
		Path outputPath = FileSystems.getDefault().getPath(path + "/" + artifact_path);
		Path outputPathArtifact = FileSystems.getDefault().getPath(outputPath + "/" + artifact);
		
		try 
		{
			Files.createDirectories(outputPath);
			Files.copy(uploadedInputStream, outputPathArtifact, options);
			return Response.ok().build();
		} 
		catch (IOException e) 
		{
			logger.error(e.getLocalizedMessage());
			throw new InternalServerErrorException();
		}
	}

	public Response uploadReleaseArtifactStream(String artifact_path, String artifact_name, InputStream uploadedInputStream) {
		return uploadArtifactStream(artifact_path, artifact_name, uploadedInputStream);
	}

	public Response uploadSnapshotArtifactStream(String artifact_path, String artifact_name, InputStream uploadedInputStream) {
		return uploadArtifactStream(artifact_path, artifact_name, uploadedInputStream, StandardCopyOption.REPLACE_EXISTING);
	}
}
