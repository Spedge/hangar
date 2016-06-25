package com.spedge.hangar.repo;

/**
 * Matrix of types of repository and what language the artifacts they 
 * handle are written in.
 * 
 * @author Spedge
 *
 */
public enum RepositoryType 
{
	RELEASE_JAVA(RepositoryLanguage.JAVA),
	SNAPSHOT_JAVA(RepositoryLanguage.JAVA),
	PROXY_JAVA(RepositoryLanguage.JAVA),
	UNKNOWN(RepositoryLanguage.UNKNOWN);
	
	private RepositoryLanguage lang;

	RepositoryType(RepositoryLanguage lang)
	{
		this.lang = lang;
	}
	
	public RepositoryLanguage getLanguage()
	{
		return lang;
	}
}
