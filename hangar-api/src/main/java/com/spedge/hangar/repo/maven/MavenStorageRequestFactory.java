package com.spedge.hangar.repo.maven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.spedge.hangar.config.ArtifactLanguage;
import com.spedge.hangar.proxy.ProxyRequest;
import com.spedge.hangar.repo.RepositoryType;
import com.spedge.hangar.repo.StorageRequestFactory;
import com.spedge.hangar.repo.java.base.JavaGroup;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.request.StorageRequest;
import com.spedge.hangar.storage.request.StorageRequestKey;

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
    
    public JavaIndexKey convertKeys(RepositoryType type, StorageRequestKey key)
    {
        String[] index = key.getFullKey();
        
        // What we want to do here is, if the prefix has been left in the request key, 
        // remove it so that we focus on the Java Group.
        int startIndex = (prefix.get(0) == index[0] && prefix.get(1) == index[1])? 2 : 0;
        
        JavaGroup group = JavaGroup.arrayDelimited(Arrays.copyOfRange(index, startIndex, index.length - 3));
        String artifact = index[index.length - 3];
        String version = index[index.length - 2];
        
        return new JavaIndexKey(type, group, artifact, version);
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
    
    /**
     * Request all the StorageRequestKeys for a given path of storage.
     * @return StorageRequest that will return all the given StorageKeys
     */
    public StorageRequest downloadKeysRequest()
    {
        return new StorageRequest.StorageRequestBuilder()
                   .language(lang)
                   .index(prefix.toArray(new String[0]))
                   .build();
    }
}
