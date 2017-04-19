package com.spedge.hangar.repo;

import com.spedge.hangar.config.ArtifactLanguage;

/**
 * Matrix of types of repository and what language the artifacts they handle are
 * written in.
 * 
 * @author Spedge
 *
 */
public enum RepositoryType
{
    RELEASE_JAVA(ArtifactLanguage.JAVA), 
    SNAPSHOT_JAVA(ArtifactLanguage.JAVA), 
    PROXY_JAVA(ArtifactLanguage.JAVA), 
    PROXY_PYTHON(ArtifactLanguage.PYTHON),
    UNKNOWN(ArtifactLanguage.UNKNOWN);

    private ArtifactLanguage lang;

    RepositoryType(ArtifactLanguage lang)
    {
        this.lang = lang;
    }

    public ArtifactLanguage getLanguage()
    {
        return lang;
    }
}
