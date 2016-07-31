package com.spedge.hangar.repo.java.api;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

public class JavaDownloadEndpoint extends JavaRepository
{
    private RepositoryType repositoryType = RepositoryType.PROXY_JAVA;
    private String[] proxies;

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
    @Path("/{group : .+}/{artifact : .+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public StreamingOutput getToplevelMetadata(@PathParam("group") String group,
                                               @PathParam("artifact") String artifact, 
                                               @PathParam("type") String type)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, "metadata");
        return getArtifact(key, "maven-metadata.xml" + type);
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
    @Path("/{group : .+}/{artifact : .+}/{version : ([.\\d\\.]*\\-SNAPSHOT)+}/maven-metadata.xml{type : (\\.)?(\\w)*}")
    public StreamingOutput getMetadata(@PathParam("group") String group,
                                       @PathParam("artifact") String artifact, 
                                       @PathParam("version") String version,
                                       @PathParam("type") String type)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
        return getArtifact(key, "maven-metadata.xml" + type);
    }
    
    /**
     * Retrieves an Artifact from storage
     * Example Path : /java/com/spedge/hangar-artifact/1.0.0/hangar-artifact-1.0.0.jar
     * 
     * @param group Group of the Artifact
     * @param version Version of the Artifact
     * @param artifact Name of the Artifact
     * @param filename Exact filename required
     * @return StreamingOutput containing the content of the Artifact requested.
     */
    @GET
    @Path("/{group : .+}/{artifact : .+}/{version : .+}/{filename : [^/]+}")
    public StreamingOutput getArtifact(@PathParam("group") String group,
                                       @PathParam("version") String version,
                                       @PathParam("artifact") String artifact,
                                       @PathParam("filename") String filename)
    {
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact, version);
        
        try
        {
            return getArtifact(key, filename);
        }
        catch (NotFoundException nfe)
        {
            return getProxyArtifact(proxies, key, filename);
        }
    }

    public String[] getProxy()
    {
        return proxies;
    }

    public void setProxy(String[] proxy)
    {
        this.proxies = proxy;
    }

    @Override
    public RepositoryType getType()
    {
        return repositoryType;
    }
}
