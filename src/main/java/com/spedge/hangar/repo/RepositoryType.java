package com.spedge.hangar.repo;

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
