package com.spedge.hangar.repo.java.api;

import com.google.common.io.ByteStreams;

import com.amazonaws.util.IOUtils;

import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.JavaRepository;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.StorageException;
import com.spedge.hangar.storage.StorageRequest;

import org.eclipse.jetty.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

public class JavaDownloadEndpoint extends JavaRepository
{

    private RepositoryType repositoryType = RepositoryType.PROXY_JAVA;
    private String[] proxy;

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
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact,
                "metadata");
        logger.debug("[Downloading Metadata] " + key.toString());

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
        JavaIndexKey key = new JavaIndexKey(repositoryType, group.replace('/', '.'), artifact,
                version);
        logger.info("[Downloading Artifact] " + key);

        try
        {
            return getArtifact(key, filename);
        }
        catch (NotFoundException nfe)
        {
            try
            {
                for (String source : proxy)
                {
                    // So the artifact doesn't exist. We try and download it and
                    // save it to disk.
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(source)
                            .path(group + "/" + artifact + "/" + version + "/" + filename);

                    Invocation.Builder builder = target.request(MediaType.WILDCARD);
                    Response resp = builder.get();

                    if (resp.getStatus() == HttpStatus.OK_200)
                    {
                        logger.info("[Proxy] Downloading from " + source);

                        // We need to load it into memory. We'll look at doing
                        // this another way
                        // (perhaps to disk first) but I'd rather download then
                        // upload to S3 and back to the client
                        // concurrently. Not sure if this is possible but it'd
                        // save a bunch of time.
                        InputStream in = resp.readEntity(InputStream.class);

                        byte[] byteArray = IOUtils.toByteArray(in);
                        resp.close();

                        // Now upload the artifact to our proxy location.
                        StorageRequest sr = new StorageRequest();
                        sr.setFilename(filename);
                        sr.setStream(new ByteArrayInputStream(byteArray));
                        sr.setLength(resp.getLength());
                        
                        // Ok now we've downloaded the thing, we'll use it.
                        IndexArtifact ia = getStorage().generateArtifactPath(getType(), getPath(),
                                key);
                        getStorage().uploadSnapshotArtifactStream(ia, sr);

                        // We should add it to the index now. Most of these
                        // don't have metadata, so we
                        // add it to the index as soon as we get it.
                        getIndex().addArtifact(key, ia);

                        // We take our copy and return it to the user
                        // post-upload.
                        final InputStream writer = new ByteArrayInputStream(byteArray);
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
                }

                throw new NotFoundException();
            }
            catch (StorageException | IndexConfictException | IndexException | IOException exp)
            {
                throw new InternalServerErrorException();
            }
        }
    }

    public String[] getProxy()
    {
        return proxy;
    }

    public void setProxy(String[] proxy)
    {
        this.proxy = proxy;
    }

    @Override
    public RepositoryType getType()
    {
        return repositoryType;
    }
}
