/**
 * Copyright 2013 Ben Navetta <ben.navetta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

/**
 * JPA-based implementation of the {@link File} interface.
 * @author ben
 */
@Entity
public class FileImpl implements File
{
	/**
	 * The path of the file.
	 * @see File#getPath()
	 */
	@Id
	private String path;
	
	/**
	 * A pointer to this file's parent.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PARENT_ID")
	private File parent;
	
	/**
	 * The name of the file system that this file belongs to.
	 * @see #getFileSystem()
	 */
	private String namespace;
	
	/**
	 * The identifier of the blob containing this file's content.
	 */
	private BlobKey blobKey;
	
	/**
	 * The MD5 hash of this file's content.
	 * @see File#getMD5Hash()
	 */
	private byte[] md5Hash;
	
	/**
	 * The text representation of this file's MIME type since the {@link MediaType} class is not a valid datastore type.
	 * The actual MIME type is copied into this field before persisting, and restored upon load.
	 */
	private String mimeTypeString;
	
	/**
	 * The actual MIME type of the file. It is transient because DataNucleus cannot persist it.
	 * @see File#getMimeType()
	 */
	@Transient
	private MediaType mimeType;
	
	/**
	 * Empty constructor for JPA.
	 */
	public FileImpl() {}
	
	/**
	 * Create a {@link MediaType} from the value of {@link #mimeTypeString}.
	 * @see MediaType#parse(String)
	 */
	@PostLoad
	final void restoreMimeType()
	{
		if(!Strings.isNullOrEmpty(mimeTypeString))
		{
			mimeType = MediaType.parse(mimeTypeString);
		}
	}
	
	/**
	 * Set {@link #mimeTypeString} to the string representation of {@link #mimeType}.
	 * @see MediaType#toString()
	 */
	@PreUpdate
	@PrePersist
	final void storeMimeType()
	{
		if(mimeType != null)
		{
			mimeTypeString = mimeType.toString();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName()
	{
		return lastPathComponent(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPath()
	{
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FileSystem getFileSystem()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final File getParent()
	{
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isDirectory()
	{
		return blobKey == null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final MediaType getMimeType()
	{
		return mimeType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final byte[] getMD5Hash()
	{
		final byte[] out = new byte[md5Hash.length];
		System.arraycopy(md5Hash, 0, out, 0, md5Hash.length);
		return out;
	}

}
