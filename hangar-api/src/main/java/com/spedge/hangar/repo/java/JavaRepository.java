package com.spedge.hangar.repo.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;
import com.spedge.hangar.index.IndexArtifact;
import com.spedge.hangar.index.IndexConfictException;
import com.spedge.hangar.index.IndexException;
import com.spedge.hangar.proxy.ProxyRequest;
import com.spedge.hangar.proxy.RemoteMavenProxy;
import com.spedge.hangar.repo.RepositoryBase;
import com.spedge.hangar.repo.java.healthcheck.JavaRepositoryHealthcheck;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.maven.MavenStorageRequestFactory;
import com.spedge.hangar.storage.request.StorageRequest;
import com.spedge.hangar.storage.request.StorageRequestException;
import com.spedge.hangar.storage.request.StorageRequestKey;

@Path("/java")
public abstract class JavaRepository extends RepositoryBase
{
    protected final Logger logger = LoggerFactory.getLogger(JavaRepository.class);
    private JavaRepositoryHealthcheck check;
    private MavenStorageRequestFactory factory;

    public JavaRepository()
    {
        this.check = new JavaRepositoryHealthcheck();
        this.factory = new MavenStorageRequestFactory(getId());
    }

    public void createFactory()
    {
        this.factory = new MavenStorageRequestFactory(getId());
    }

    /**
     * Returns the health checks for this repository.
     */
    public Map<String, HealthCheck> getHealthChecks()
    {
        Map<String, HealthCheck> checks = new HashMap<String, HealthCheck>();
        checks.put("java_repo", check);
        checks.put("java_storage", getStorage().getHealthcheck());
        return checks;
    }

    @Override
    public void reloadIndex()
    {
        StorageRequest sr = factory.downloadKeysRequest();

        try
        {
            int complete = 0;
            int conflict = 0;
            int exception = 0;
            
            for (StorageRequestKey key : super.getStorage().getArtifactKeys(sr))
            {
                try
                {
                    addArtifactToIndex(key, new JavaIndexArtifact(key.getKey("/")));
                    complete++;
                }
                catch (IndexConfictException ice)
                {
                    ice.printStackTrace();
                    conflict++;
                }
                catch (IndexException ie)
                {
                    ie.printStackTrace();
                    exception++;
                }
            }

            logger.info("Loaded " + complete + " keys, " + conflict + " conflicts and " + exception
                            + " errors.");
        }
        catch (StorageRequestException sre)
        {
            // TODO Auto-generated catch block
            sre.printStackTrace();
        }
    }

    protected StreamingOutput getArtifact(JavaIndexKey key, String filename)
    {
        if (Pattern.matches("[.\\d\\.]*-SNAPSHOT", key.getVersion()))
        {
            logger.info("[Downloading Snapshot] " + key.toString());
            return getSnapshotArtifact(key, filename);
        }
        else
        {
            try
            {
                // Let's check if the file exists in our index.
                // If it doesn't, we tell the requester that it's not found.
                if (getIndex().isArtifact(key))
                {
                    StorageRequest sr = factory.downloadArtifactRequest(key, filename);

                    try
                    {
                        return getStorage().getArtifactStream(sr);
                    }
                    catch (StorageRequestException se)
                    {
                        throw new NotFoundException();
                    }
                }
                else
                {
                    throw new NotFoundException();
                }
            }
            catch (IndexException ie)
            {
                throw new InternalServerErrorException();
            }
        }
    }

    private void addArtifactToIndex(StorageRequestKey key, IndexArtifact jia)
                    throws IndexConfictException, IndexException
    {
        JavaIndexKey jik = factory.convertKeys(getType(), key);
        getIndex().addArtifact(jik, jia);
    }

    private void addArtifactToIndex(StorageRequest sr) throws IndexConfictException, IndexException
    {
        addArtifactToIndex(sr.getKey(), new JavaIndexArtifact(sr.getKey().getKey("/")));
    }

    /**
     * This version is different as we need to re-write the filename with the
     * timestamp for the latest version.
     * 
     * @param key
     *            IndexKey to find the Artifact in the Index
     * @param filename
     *            Filename from the request
     * @return StreamingOutput from the Storage Layer
     */
    protected StreamingOutput getSnapshotArtifact(JavaIndexKey key, String filename)
    {
        logger.info("[Downloading Snapshot] " + key);
        try
        {
            if (getIndex().isArtifact(key))
            {
                JavaIndexArtifact ia = (JavaIndexArtifact) getIndex().getArtifact(key);
                String snapshotFilename = filename.replace(key.getVersion(),
                                ia.getSnapshotVersion());

                return getStorage().getArtifactStream(
                                factory.downloadArtifactRequest(key, snapshotFilename));
            }
            else
            {
                throw new NotFoundException();
            }
        }
        catch (IndexException ie)
        {
            throw new InternalServerErrorException();
        }
        catch (StorageRequestException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieve an artifact, starting with our local artifact store. If it
     * doesn't exist, prepare a request and retrieve the artifact from storage.
     * 
     * @param proxies
     *            Array of potential proxy sources
     * @param key
     *            IndexKey for the requested artifact
     * @param filename
     *            The filename of the file requested
     * @return StreamingOutput of the file
     */
    public StreamingOutput getProxiedArtifact(String[] proxies, JavaIndexKey key, String filename)
    {
        try
        {
            return getArtifact(key, filename);
        }
        catch (NotFoundException nfe)
        {
            try
            {
                // We create an artifact request to work with a remote proxy,
                // much in the same as we would a normal storage request.
                ProxyRequest pr = factory.proxyArtifactRequest(proxies, key, filename);
                RemoteMavenProxy.requestProxiedArtifact(pr);

                // However, once we've got the item, the path we want to store
                // it
                // in is slightly wrong, so we need to re-set it back to a
                // StorageRequest.
                StorageRequest sr = factory.downloadArtifactRequest(key, filename, pr);

                addArtifactToStorage(sr);
                addArtifactToIndex(sr);

                return sr.getStreamingOutput();
            }
            catch (NotFoundException nfee)
            {
                return createChecksum(key, filename);
            }
            catch (IndexConfictException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            catch (IndexException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * If there is not a checksum file uploaded as part of the artefact (which,
     * really, you should do) then we assume what we've got is valid and create
     * a checksum file for the client to check the download.
     * 
     * @param key
     *            Key of the Checksum file missing
     * @param filename
     *            Filename of the checksum file missing
     * @return StreamingOutput
     */
    private StreamingOutput createChecksum(JavaIndexKey key, String filename)
    {
        if (filename.endsWith(".sha1"))
        {
            return createChecksum(key, filename, new ChecksumWrapper()
            {

                @Override
                String getTargetFilename(String filename)
                {
                    return filename.subSequence(0, (filename.length() - 5)).toString();
                }

                @Override
                String getDigestString(ByteArrayOutputStream out)
                {
                    return DigestUtils.sha1Hex(out.toByteArray());
                }

                @Override
                String getType()
                {
                    return "SHA1";
                }
            });
        }
        else if (filename.endsWith(".md5"))
        {
            return createChecksum(key, filename, new ChecksumWrapper()
            {

                @Override
                String getTargetFilename(String filename)
                {
                    return filename.subSequence(0, (filename.length() - 4)).toString();
                }

                @Override
                String getDigestString(ByteArrayOutputStream out)
                {
                    return DigestUtils.md5Hex(out.toByteArray());
                }

                @Override
                String getType()
                {
                    return "MD5";
                }
            });
        }
        else
        {
            throw new NotFoundException();
        }
    }

    private StreamingOutput createChecksum(JavaIndexKey key, String filename,
                    ChecksumWrapper checksumWrapper)
    {
        try
        {
            StreamingOutput so = getArtifact(key, checksumWrapper.getTargetFilename(filename));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            so.write(out);

            String checksum = checksumWrapper.getDigestString(out);
            out.close();

            logger.info("[Warning] No checksum found, generated " + checksumWrapper.getType()
                            + " Checksum : " + checksum);

            StorageRequest sr = new StorageRequest.StorageRequestBuilder().length(checksum.length())
                            .stream(checksum.getBytes()).filename(filename).build();

            addArtifactToStorage(sr);
            return sr.getStreamingOutput();
        }
        catch (IOException ioe)
        {
            logger.error("[ERROR] Could not generate checksum for : " + filename + ", "
                            + ioe.getMessage());
            throw new NotFoundException();
        }
    }

    abstract class ChecksumWrapper
    {
        abstract String getTargetFilename(String filename);

        abstract String getType();

        abstract String getDigestString(ByteArrayOutputStream out);
    }
}
