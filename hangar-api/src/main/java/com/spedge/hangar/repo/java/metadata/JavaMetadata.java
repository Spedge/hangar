package com.spedge.hangar.repo.java.metadata;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metadata")
public class JavaMetadata
{

    private JavaMetadataVersioning versioning;

    public JavaMetadataVersioning getVersioning()
    {
        return versioning;
    }

    @XmlElement(name = "versioning")
    public void setVersioning(JavaMetadataVersioning versioning)
    {
        this.versioning = versioning;
    }
}
