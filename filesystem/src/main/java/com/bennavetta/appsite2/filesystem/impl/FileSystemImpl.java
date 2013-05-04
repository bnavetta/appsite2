package com.bennavetta.appsite2.filesystem.impl;

import java.util.Collection;

import com.bennavetta.appsite2.filesystem.File;
import com.bennavetta.appsite2.filesystem.FileSystem;
import com.bennavetta.appsite2.filesystem.FileSystemException;
import com.bennavetta.appsite2.filesystem.util.FileInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.net.MediaType;

public class FileSystemImpl implements FileSystem
{
	private final String name;
	
	public FileSystemImpl(final String name)
	{
		this.name = name;
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public Collection<File> listFiles(File directory)
	{
		return null;
	}

	@Override
	public File fileAt(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File relativeTo(File base, String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(File file, boolean recurse) throws FileSystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(String path, MediaType mimeType, BlobKey blobKey,
			byte[] md5) throws FileSystemException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(FileInfo info) throws FileSystemException {
		// TODO Auto-generated method stub
		
	}

}
