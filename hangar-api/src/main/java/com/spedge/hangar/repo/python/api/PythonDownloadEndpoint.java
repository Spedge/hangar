package com.spedge.hangar.repo.python.api;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.StreamingOutput;

import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.repo.python.PythonIndexKey;
import com.spedge.hangar.repo.python.PythonRepository;

public class PythonDownloadEndpoint extends PythonRepository
{
    private String[] proxies;
    
    @GET
    @Path("/simple/{artifact : .+}/")
    public StreamingOutput getArtifact(@PathParam("artifact") String artifact)
    {
        return super.getProxiedArtifact(proxies, "/simple/" + artifact);       
    }
    
    @GET
    @Path("/packages/{base1 : .+}/{base2 : .+}/{baseMax : .+}/{artifact : .+}")
    public StreamingOutput getPackage(@PathParam("base1") String base1,
                                      @PathParam("base2") String base2,
                                      @PathParam("baseMax") String baseMax,
                                      @PathParam("artifact") String artifact)
    {
        PythonIndexKey pk = new PythonIndexKey(getType(), base1, base2, baseMax, artifact);
        return super.getProxiedArtifact(proxies, "/packages" + pk.getPath());       
    }

    @Override
    public RepositoryType getType()
    {
        return RepositoryType.PROXY_PYTHON;
    }
    
    public String[] getProxy()
    {
        return proxies;
    }

    public void setProxy(String[] proxy)
    {
        this.proxies = proxy;
    }
}
