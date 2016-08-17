package com.spedge.hangar.repo.java.api;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaSnapshotRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageRequest;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

public class JavaSnapshotEndpoint extends JavaSnapshotRepository
{
    private RepositoryType repositoryType = RepositoryType.SNAPSHOT_JAVA;

    /**
     * Retrieves an Snapshot Artifact from storage
     * Example Path : /snapshots/java/com/spedge/hangar-artifact/1.0.0-SNAPSHOT/hangar-artifact-1.0.0-SNAPSHOT.jar
     * 
     * @param group Group of the Artifact
     * @param version Version of the Artifact
     * @param artifact Name of the Artifact
     * @param filename Exact filename required
     * @return StreamingOutput containing the content of the Artifact requested.
     */
    @GET
    @Path("/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+-SNAPSHOT}/{filename : [^/]+}")
    public StreamingOutput getSnapshotArtifact(@PathParam("group") String group,
                                               @PathParam("artifact") String artifact, 
                                               @PathParam("version") String version,
                                               @PathParam("filename") String filename)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
        logger.debug("[Downloading Snapshot] " + key);

        return getSnapshotArtifact(key, filename);
    }
        
    /**
     * Uploads an Snapshot Artifact to storage
     * Example Path : /snapshots/java/com/spedge/hangar-artifact/1.0.0-SNAPSHOT/hangar-artifact-1.0.0-SNAPSHOT.jar
     * 
     * @param group Group of the Artifact
     * @param version Version of the Artifact
     * @param artifact Name of the Artifact
     * @param filename Exact filename required
     * @param uploadedInputStream Contents of the artifact
     * @return A response with a status code
     */
    @PUT
    @Consumes(MediaType.WILDCARD)
    @Path("/snapshots/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+-SNAPSHOT}/{filename : [^/]+}")
    public Response uploadArtifact(@Context final HttpServletRequest request,
                                   @PathParam("group") String group, 
                                   @PathParam("artifact") String artifact,
                                   @PathParam("version") String version, 
                                   @PathParam("filename") String filename,
                                   InputStream uploadedInputStream)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
        logger.debug("[Uploading Snapshot] " + key.toString());

        try
        {            
            StorageRequest sr = new StorageRequest.StorageRequestBuilder()
                                                  .length(request.getContentLength())
                                                  .stream(uploadedInputStream)
                                                  .filename(filename)
                                                  .build();
            
            return addArtifact(key, sr);
        }
        catch (IOException ioe)
        {
            throw new InternalServerErrorException(ioe);
        }
    }

    /**
     * This allows us to download the top level metadata - which won't have a version. 
     * Example Path : /com/spedge/hangar-artifact/maven-metadata.xml
     * 
     * @param group Group of Metadata
     * @param artifact Artifact of Metadata
     * @param type Used when requesting the sha or md5 of the metadata
     * @return A StreamingOutput containing the content of the metadata requested.
     */
    @GET
    @Path("/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public StreamingOutput getToplevelMetadata(@PathParam("group") String group,
                                               @PathParam("artifact") String artifact, 
                                               @PathParam("type") String type)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact,
                "metadata");
        logger.debug("[Downloading Metadata] " + key.toString());

        return getArtifact(key, "maven-metadata.xml" + type);
    }

    /**
     * Uploads a new version of the top level metadata - which won't have a version. 
     * Example Path : /com/spedge/hangar-artifact/maven-metadata.xml
     * 
     * @param group Group of Metadata
     * @param artifact Artifact of Metadata
     * @param type Used when uploading the sha or md5 of the metadata
     * @param uploadedInputStream Contents of the artifact
     * @return A response with a status code
     */
    @PUT
    @Consumes(MediaType.WILDCARD)
    @Path("/snapshots/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public Response uploadTopLevelMetadata(@Context final HttpServletRequest request,
                                           @PathParam("group") String group,
                                           @PathParam("artifact") String artifact,
                                           @PathParam("type") String type, 
                                           InputStream uploadedInputStream)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, "metadata");
        logger.debug("[Uploading Metadata] " + key.toString());

        try
        {
            StorageRequest sr = new StorageRequest.StorageRequestBuilder()
                            .length(request.getContentLength())
                            .stream(uploadedInputStream)
                            .filename("maven-metadata.xml" + type)
                            .build();
                        
            if (!type.isEmpty())
            {
                return addArtifact(key, sr);
            }
            else
            {
                return addMetadata(key, sr);
            }
        }
        catch (IOException ioe)
        {
            throw new InternalServerErrorException(ioe);
        }
    }

    /**
     * <p>This allows us to download the snapshot metadata which allows us to determine
     * the latest version of the snapshot.
     * 
     * Example Path : /com/spedge/hangar-artifact/1.0.0-SNAPSHOT/maven-metadata.xml</p>
     * 
     * @param group Group of Metadata
     * @param artifact Artifact of Metadata
     * @param version Version of the Metadata
     * @param type Used when requesting the sha or md5 of the metadata
     * @return A StreamingOutput containing the content of the metadata requested.
     */
    @GET
    @Path("/snapshots/{group : .+}/{artifact : .+}/{version : ([\\d\\.]*\\-SNAPSHOT)+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public StreamingOutput getMetadata(@PathParam("group") String group,
                                       @PathParam("artifact") String artifact, 
                                       @PathParam("version") String version,
                                       @PathParam("type") String type)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact,
                version);
        logger.debug("[Downloading Metadata] " + key.toString());

        return getArtifact(key, "maven-metadata.xml" + type);
    }

    /**
     * Uploads a new version of the snapshot metadata. 
     * 
     * @param group Group of Metadata
     * @param artifact Artifact of Metadata
     * @param type Used when uploading the sha or md5 of the metadata
     * @param uploadedInputStream Contents of the artifact
     * @return A response with a status code
     */
    @PUT
    @Consumes(MediaType.WILDCARD)
    @Path("/snapshots/{group : .+}/{artifact : .+}/{version : ([\\d\\.]*\\-SNAPSHOT)+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public Response uploadMetadata(@Context final HttpServletRequest request,
                                   @PathParam("group") String group, 
                                   @PathParam("artifact") String artifact,
                                   @PathParam("version") String version, 
                                   @PathParam("type") String type,
                                   InputStream uploadedInputStream)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
        logger.debug("[Uploading Metadata] " + key.toString());

        try
        {
            StorageRequest sr = new StorageRequest.StorageRequestBuilder()
                            .length(request.getContentLength())
                            .stream(uploadedInputStream)
                            .filename("maven-metadata.xml" + type)
                            .build();
    
            if (!type.isEmpty())
            {
                return addArtifact(key, sr);
            }
            else
            {
                return addSnapshotMetadata(key, sr);
            }
        }
        catch (IOException ioe)
        {
            throw new InternalServerErrorException(ioe);
        }
    }

    public RepositoryType getType()
    {
        return repositoryType;
    }
}
