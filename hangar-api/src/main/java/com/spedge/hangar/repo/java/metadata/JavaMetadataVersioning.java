package com.spedge.hangar.repo.java.metadata;

import javax.xml.bind.annotation.XmlElement;

public class JavaMetadataVersioning
{

    private JavaMetadataVersioningSnapshot snapshot;
    private String release;

    public JavaMetadataVersioningSnapshot getSnapshot()
    {
        return snapshot;
    }

    public String getLatestReleaseVersion()
    {
        return release;
    }

    @XmlElement(name = "release")
    public void setRelease(String release)
    {
        this.release = release;
    }

    @XmlElement(name = "snapshot")
    public void setSnapshot(JavaMetadataVersioningSnapshot snapshot)
    {
        this.snapshot = snapshot;
    }
}
