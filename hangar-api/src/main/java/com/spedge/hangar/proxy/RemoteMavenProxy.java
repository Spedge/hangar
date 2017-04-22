package com.spedge.hangar.proxy;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteMavenProxy
{
    protected static final Logger logger = LoggerFactory.getLogger(RemoteMavenProxy.class);

    public static ProxyRequest requestProxiedArtifact(ProxyRequest pr)
    {
        try
        {
            for (String source : pr.getProxies())
            {
                // So the artifact doesn't exist. We try and download it and
                // save it to disk.
                ClientConfig configuration = new ClientConfig();
                configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 60000);
                configuration = configuration.property(ClientProperties.READ_TIMEOUT, 60000);

                Client client = ClientBuilder.newClient(configuration);
                WebTarget target = client.target(source).path(pr.getRemotePath());

                logger.info("[Downloading Proxied Artifact] " + target.getUri());

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
                    pr.saveResponse(resp.getStatus(), resp.getLength(), byteArray);
                    
                    return pr;
                }
            }
            throw new NotFoundException();
        }
        catch (IOException exp)
        {
            throw new InternalServerErrorException();
        }
    }
}
