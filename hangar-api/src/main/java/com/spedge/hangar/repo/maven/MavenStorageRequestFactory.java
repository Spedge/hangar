package com.spedge.hangar.repo.maven;

import com.spedge.hangar.config.ArtifactLanguage;
import com.spedge.hangar.repo.StorageRequestFactory;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.request.StorageRequest;

public class MavenStorageRequestFactory extends StorageRequestFactory
{
    private static final ArtifactLanguage lang = ArtifactLanguage.JAVA;
    private String id;
    
    public MavenStorageRequestFactory(String id)
    {
        this.id = "maven." + id + ".";
    }

    /**
     * Builds a StorageRequest to request an Artifact
     * @param key The JavaIndexKey of the artifact to download.
     * @return A StorageRequest with enough information to achieve a download if it exists.
     */
    public StorageRequest downloadArtifactRequest(JavaIndexKey key)
    {
        return new StorageRequest.StorageRequestBuilder()
                   .index(lang, ".", id + key.toString())
                   .build();
    }

    public StorageRequest downloadKeysRequest()
    {
        return new StorageRequest.StorageRequestBuilder()
                   .index(lang, ".", id)
                   .build();
    }
}
