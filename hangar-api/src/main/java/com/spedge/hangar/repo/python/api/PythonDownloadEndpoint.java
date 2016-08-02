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
    @Path("/{artifact : .+}/")
    public StreamingOutput getArtifact(@PathParam("artifact") String artifact)
    {
        PythonIndexKey pk = new PythonIndexKey(getType(), artifact);
    
        return super.getProxiedArtifact(proxies, pk, artifact);       
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
