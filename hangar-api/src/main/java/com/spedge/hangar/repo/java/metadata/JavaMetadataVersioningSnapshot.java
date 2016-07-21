package com.spedge.hangar.repo.java.metadata;

import javax.xml.bind.annotation.XmlElement;

public class JavaMetadataVersioningSnapshot
{

    private String timestamp;
    private int buildNumber;

    public String getVersion()
    {
        return timestamp;
    }

    @XmlElement(name = "timestamp")
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getBuildNumber()
    {
        return buildNumber;
    }

    @XmlElement(name = "buildNumber")
    public void setBuildNumber(int buildNumber)
    {
        this.buildNumber = buildNumber;
    }
}
