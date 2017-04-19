package com.spedge.hangar.repo.maven;

import com.spedge.hangar.config.ArtifactLanguage;
import com.spedge.hangar.repo.java.index.JavaIndexKey;
import com.spedge.hangar.storage.request.StorageRequest;

public class MavenStorageRequestFactory
{
    private static final ArtifactLanguage lang = ArtifactLanguage.JAVA;
    private static final String PATH = "maven.releases.";
    
    /**
     * Builds a StorageRequest to request an Artifact
     * @param key The JavaIndexKey of the artifact to download.
     * @return A StorageRequest with enough information to achieve a download if it exists.
     */
    public static StorageRequest downloadArtifactRequest(JavaIndexKey key)
    {
        return new StorageRequest.StorageRequestBuilder()
                   .index(lang, ".", PATH + key.toString())
                   .build();
    }
}
