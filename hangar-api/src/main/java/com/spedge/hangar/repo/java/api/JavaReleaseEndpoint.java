package com.spedge.hangar.repo.java.api;

import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaReleaseRepository;
import com.spedge.hangar.repo.java.JavaStorageTranslator;
import com.spedge.hangar.repo.java.base.JavaGroup;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.IStorageTranslator;
import com.spedge.hangar.storage.request.StorageRequest;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

public class JavaReleaseEndpoint extends JavaReleaseRepository
{
    private RepositoryType repositoryType = RepositoryType.RELEASE_JAVA;

    /**
     * Retrieves an Release Artifact from storage
     * Example Path : /releases/java/com/spedge/hangar-artifact/1.0.0/hangar-artifact-1.0.0.jar
     * 
     * @param group Group of the Artifact
     * @param version Version of the Artifact
     * @param artifact Name of the Artifact
     * @param filename Exact filename required
     * @return StreamingOutput containing the content of the Artifact requested.
     */
    @GET
    @Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+}/{filename : [^/]+}")
    public StreamingOutput getArtifact(@PathParam("group") String group,
                                       @PathParam("artifact") String artifact, 
                                       @PathParam("version") String version,
                                       @PathParam("filename") String filename)
    {
        JavaGroup jg = JavaGroup.slashDelimited(group);
        JavaIndexKey key = new JavaIndexKey(repositoryType, jg, artifact, version);
        logger.debug("[Downloading Release] " + key);

        return getArtifact(key, filename);
    }

    /**
     * Uploads an Release Artifact to storage
     * Example Path : /releases/java/com/spedge/hangar-artifact/1.0.0/hangar-artifact-1.0.0.jar
     * 
     * @param group Group of the Artifact
     * @param version Version of the Artifact
     * @param artifact Name of the Artifact
     * @param filename Exact filename required
     * @param uploadedInputStream Contents of the artifact
     * @return A response with a status code
     */
//    @PUT
//    @Consumes(MediaType.WILDCARD)
//    @Path("/releases/{group : .+}/{artifact : .+}/{version : (?i)[\\d\\.]+}/{filename : [^/]+}")
//    public Response uploadArtifact(@Context final HttpServletRequest request,
//                                   @PathParam("group") String group, 
//                                   @PathParam("artifact") String artifact,
//                                   @PathParam("version") String version,
//                                   @PathParam("filename") String filename,
//                                   InputStream uploadedInputStream)
//    {
//        JavaGroup jg = JavaGroup.slashDelimited(group);
//        JavaIndexKey key = new JavaIndexKey(repositoryType, jg, artifact, version);
//        logger.debug("[Uploading Release] " + key.toString());
//
//        try
//        {
//            if (getIndex().isArtifact(key))
//            {
//                if (getIndex().getArtifact(key).isStoredFile(filename))
//                {
//                    throw new WebApplicationException(Status.CONFLICT);
//                }
//            }
//            
//            StorageRequest sr = new StorageRequest.StorageRequestBuilder()
//                            .length(request.getContentLength())
//                            .stream(uploadedInputStream)
//                            .filename(filename)
//                            .build();
//            
//            return addArtifact(key, sr);
//        }
//        catch (IndexException | IOException exc)
//        {
//            throw new InternalServerErrorException(exc);
//        }       
//    }

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
    @Path("/releases/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public StreamingOutput getToplevelMetadata(@PathParam("group") String group,
                                               @PathParam("artifact") String artifact, 
                                               @PathParam("type") String type)
    {
        JavaGroup jg = JavaGroup.slashDelimited(group);
        JavaIndexKey key = new JavaIndexKey(repositoryType, jg, artifact, "metadata");
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
//    @PUT
//    @Consumes(MediaType.WILDCARD)
//    @Path("/releases/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
//    public Response uploadTopLevelMetadata(@Context final HttpServletRequest request,
//                                           @PathParam("group") String group, 
//                                           @PathParam("artifact") String artifact,
//                                           @PathParam("type") String type, 
//                                           InputStream uploadedInputStream)
//    {
//        JavaGroup jg = JavaGroup.slashDelimited(group);
//        JavaIndexKey key = new JavaIndexKey(repositoryType, jg, artifact, "metadata");
//        logger.debug("[Uploading Metadata] " + key.toString());
//
//        try
//        {
//            StorageRequest sr = new StorageRequest.StorageRequestBuilder()
//                                                  .length(request.getContentLength())
//                                                  .stream(uploadedInputStream)
//                                                  .filename("maven-metadata.xml" + type)
//                                                  .build();
//
//            if (!type.isEmpty())
//            {
//                return addArtifact(key, sr);
//            }
//            else
//            {
//                return addReleaseMetadata(key, sr);
//            }
//        }
//        catch (IOException exc)
//        {
//            throw new InternalServerErrorException(exc);
//        }    
//    }

    @Override
    public RepositoryType getType()
    {
        return repositoryType;
    }
}
