package com.spedge.hangar.index;

import java.util.HashMap;
import java.util.Map;

public class ReservedArtifact extends IndexArtifact{

	private static final long serialVersionUID = -5676826253822808582L;
	private long timestamp;
	private Map<String, Boolean> files = new HashMap<String, Boolean>();
	
	public ReservedArtifact()
	{
		timestamp = System.currentTimeMillis();
	}

	@Override
	protected Map<String, Boolean> getFileTypes() {
		return files;
	}
	
	public long getTimestamp()
	{
		return timestamp;
	}
}