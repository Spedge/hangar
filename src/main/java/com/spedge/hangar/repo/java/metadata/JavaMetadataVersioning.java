package com.spedge.hangar.repo.java.metadata;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement( name = "metadata" )
public class JavaMetadataVersioning {

	private JavaMetadataVersioningSnapshot snapshot;
	
	public JavaMetadataVersioningSnapshot getSnapshot() {
		return snapshot;
	}
	
	@XmlElement( name = "snapshot" )
	public void setSnapshot(JavaMetadataVersioningSnapshot snapshot) {
		this.snapshot = snapshot;
	} 
}
