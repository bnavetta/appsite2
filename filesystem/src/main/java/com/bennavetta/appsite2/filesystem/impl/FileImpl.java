package com.bennavetta.appsite2.filesystem.impl;

import static com.bennavetta.appsite2.filesystem.util.PathUtils.lastPathComponent;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.bennavetta.appsite2.filesystem.File;
import com.bennavetta.appsite2.filesystem.FileSystem;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.base.Strings;
import com.google.common.net.MediaType;

@Entity
public class FileImpl implements File
{
	@Id
	private String path;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	private File parent;
	
	private String namespace;
	private BlobKey blobKey;
	private byte[] md5Hash;
	private String mimeTypeString;
	
	@Transient
	private MediaType mimeType;
	
	@PostLoad
	void restoreMimeType()
	{
		if(!Strings.isNullOrEmpty(mimeTypeString))
		{
			mimeType = MediaType.parse(mimeTypeString);
		}
	}
	
	@PreUpdate
	@PrePersist
	void storeMimeType()
	{
		if(mimeType != null)
		{
			mimeTypeString = mimeType.toString();
		}
	}
	
	@Override
	public String getName()
	{
		return lastPathComponent(path);
	}

	@Override
	public String getPath()
	{
		return path;
	}

	@Override
	public FileSystem getFileSystem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getParent()
	{
		return parent;
	}

	@Override
	public boolean isDirectory()
	{
		return blobKey == null;
	}

	@Override
	public MediaType getMimeType()
	{
		return mimeType;
	}

	@Override
	public byte[] getMD5Hash()
	{
		final byte[] out = new byte[md5Hash.length];
		System.arraycopy(md5Hash, 0, out, 0, md5Hash.length);
		return out;
	}

}
