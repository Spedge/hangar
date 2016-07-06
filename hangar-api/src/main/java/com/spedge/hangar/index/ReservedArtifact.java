package com.spedge.hangar.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ReservedArtifact extends IndexArtifact{

	private static final long serialVersionUID = -5676826253822808582L;
	private UUID id;
	private Map<String, Boolean> files = new HashMap<String, Boolean>();
	
	public ReservedArtifact()
	{
		id = UUID.randomUUID();
	}

	@Override
	protected Map<String, Boolean> getFileTypes() {
		return files;
	}
	
	public UUID getId()
	{
		return id;
	}
	
	@Override
	public boolean equals(Object o) 
	{
	    // self check
	    if (this == o) return true;
	    // null check
	    if (o == null) return false;
	    // type check and cast
	    if (getClass() != o.getClass()) return false;
	    
	    ReservedArtifact ra = (ReservedArtifact) o;
	    
	    return Objects.equals(getLocation(), ra.getLocation()) &&
	    	   Objects.equals(getId(), ra.getId()) &&
	    	   Objects.equals(getFileTypes(), ra.getFileTypes());
	}
}
