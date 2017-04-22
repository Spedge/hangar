package com.spedge.hangar.repo.maven;

import java.util.ArrayList;
import java.util.List;

import com.spedge.hangar.config.ArtifactLanguage;
import com.spedge.hangar.proxy.ProxyRequest;
import com.spedge.hangar.repo.StorageRequestFactory;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.request.StorageRequest;

public class MavenStorageRequestFactory extends StorageRequestFactory
{
    private static final ArtifactLanguage lang = ArtifactLanguage.JAVA;
    private List<String> prefix;
    
    public MavenStorageRequestFactory(String id)
    {
        this.prefix = new ArrayList<String>();
        this.prefix.add("maven");
        this.prefix.add(id);
    }

    /**
     * Builds a StorageRequest to request an Artifact
     * @param key The JavaIndexKey of the artifact to download.
     * @return A StorageRequest with enough information to achieve a download if it exists.
     */
    public StorageRequest downloadArtifactRequest(JavaIndexKey key, String filename)
    {
        List<String> index = new ArrayList<String>(prefix);
        index.addAll(key.getGroup().getGroupAsList());
        index.add(key.getArtifact());
        index.add(key.getVersion());
        
        return new StorageRequest.StorageRequestBuilder()
                   .language(lang)
                   .index(index.toArray(new String[0]))
                   .filename(filename)
                   .build();
    }
    

    public StorageRequest downloadArtifactRequest(JavaIndexKey key, String filename, ProxyRequest pr)
    {
        List<String> index = new ArrayList<String>(prefix);
        index.addAll(key.getGroup().getGroupAsList());
        index.add(key.getArtifact());
        index.add(key.getVersion());
        
        return new StorageRequest.StorageRequestBuilder()
                   .language(lang)
                   .index(index.toArray(new String[0]))
                   .filename(filename)
                   .stream(pr.getStream())
                   .length(pr.getLength())
                   .build();
    }
    
    /**
     * Builds a ProxyRequest to request an Artifact from a remote source
     * @param key The JavaIndexKey of the artifact to download.
     * @return A StorageRequest with enough information to achieve a download if it exists.
     */
    public ProxyRequest proxyArtifactRequest(String[] proxies, JavaIndexKey key, String filename)
    {
        List<String> index = new ArrayList<String>();
        index.addAll(key.getGroup().getGroupAsList());
        index.add(key.getArtifact());
        index.add(key.getVersion());
        index.add(filename);
        
        return new ProxyRequest(proxies, index.toArray(new String[0]));
    }
    
    public StorageRequest downloadKeysRequest()
    {
        return new StorageRequest.StorageRequestBuilder()
                   .language(lang)
                   .index(prefix.toArray(new String[0]))
                   .build();
    }

}
