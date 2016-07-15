package com.spedge.hangar.storage;

import java.nio.file.FileSystem;

import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class TestLocalStorage 
{
	@Test
	public void testStorage()
	{
		FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
		
		
	}
}
