package com.spedge.hangar.repo.python.api;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.python.PythonIndexKey;
import com.spedge.hangar.repo.python.PythonRepository;
import com.spedge.hangar.repo.python.PythonStorageTranslator;
import com.spedge.hangar.storage.IStorageTranslator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

public class PythonDownloadEndpoint extends PythonRepository
{
    private String[] proxies;
    
    /**
     * pip starts by requesting a list of all the versions for the artifact it wants
     * and doesn't give an idea of what version it's looking for. So, we need to return
     * this list - taken from Pypi but with our local artifact locations injected.
     * 
     * @param artifact Name of the artifact pip is looking for.
     * @return
     */
    @GET
    @Path("/simple/{artifact : .+}/")
    public StreamingOutput getMetadata(@PathParam("artifact") String artifact)
    {
        return super.getMetadata(proxies, artifact.replace("/", ""));       
    }
    
    /**
     * When we return the list of artifacts, if we don't have it locally the next request
     * will hit this URL. The base information parameters are required to get the artifact
     * from Pypi - which we will then save to our local storage.
     * 
     * @param base1
     * @param base2
     * @param baseMax
     * @param artifact
     * @return
     */
    @GET
    @Path("/packages/{base1 : .+}/{base2 : .+}/{baseMax : .+}/{artifact : .+}")
    public StreamingOutput getRemotePackage(@PathParam("base1") String base1,
                                            @PathParam("base2") String base2,
                                            @PathParam("baseMax") String baseMax,
                                            @PathParam("artifact") String artifact)
    {
        return super.getProxiedArtifact(proxies, "/packages/" + base1 + "/" + base2 + "/" + baseMax + "/", artifact);       
    }
    
    /** 
     * If we've got the artifact locally, the artifact URL will point at this endpoint
     * which is a much simpler format and easier to understand.
     * 
     * @param artifact
     * @param filename
     * @return
     */
    @GET
    @Path("/local/{artifact : .+}/{filename : .+}")
    public StreamingOutput getLocalPackage(@PathParam("artifact") String artifact,
                                           @PathParam("filename") String filename)
    {
        PythonIndexKey pk = new PythonIndexKey(getType(), artifact, filename);
        return super.getLocalArtifact(proxies, pk);       
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
        return RepositoryType.PROXY_PYTHON;
    }
    
    @Override
    public IStorageTranslator getStorageTranslator()
    {
        return new PythonStorageTranslator(getType());
    }
}
