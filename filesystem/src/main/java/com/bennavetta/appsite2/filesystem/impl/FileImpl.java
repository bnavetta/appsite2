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
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.bennavetta.appsite2.filesystem.File;
import com.bennavetta.appsite2.filesystem.FileSystem;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.net.MediaType;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.condition.IfNotNull;

/**
 * Objectify-based implementation of the {@link File} interface.
 * @author ben
 */
@Entity(name="file")
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
	@Load
	@Index(IfNotNull.class)
	private Ref<FileImpl> parent;
	
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
	 * The MIME type of the file. Objectify normally cannot persist it, but {@link FileSystemImpl} adds a custom converter. 
	 * @see File#getMimeType()
	 */
	private MediaType mimeType;
	
	/**
	 * Empty constructor for Objectify.
	 */
	protected FileImpl() {} // NOPMD - Objectify needs a default constructor, and there's nothing to do here
	
	/**
	 * Create a new {@code FileImpl} with the minimum information needed for a directory.
	 * @param path this file's absolute path
	 * @param parent this file's parent
	 * @param namespace the namespace of the file system this file is in
	 */
	public FileImpl(final String path, final FileImpl parent, final String namespace)
	{
		//CHECKSTYLE.OFF: MultipleStringLiterals - declaring fields for error messages is silly
		this.path = checkNotNull(path, "Path cannot be null");
		this.namespace = checkNotNull(namespace, "Namespace cannot be null");
		//CHECKSTYLE.ON: MultipleStringLiterals
		
		// parent will be null for root directory
		if(parent != null)
		{
			checkArgument(parent.namespace.equals(namespace));
			this.parent = Ref.create(parent);
		}
	}

	/**
	 * Get the namespace of this file - the name of the file system that created it.
	 * @return the namespace (never null)
	 */
	public final String getNamespace()
	{
		return namespace;
	}

	/**
	 * Set this file's namespace.
	 * @param namespace the new namespace (cannot be {@code null})
	 * @see #getNamespace()
	 */
	public final void setNamespace(final String namespace)
	{
		//CHECKSTYLE.OFF: MultipleStringLiterals - declaring fields for error messages is silly
		this.namespace = checkNotNull(namespace, "Namespace cannot be null");
		//CHECKSTYLE.ON: MultipleStringLiterals
	}

	/**
	 * Get the key identifying the blob containing this file's data.
	 * @return a blob key, or {@code null} for a directory.
	 */
	public final BlobKey getBlobKey()
	{
		return blobKey;
	}

	/**
	 * Set this file's blob key.
	 * @param blobKey the new blob key. If the blob key is {@code null}, then the file will be changed to a directory.
	 */
	public final void setBlobKey(final BlobKey blobKey)
	{
		this.blobKey = blobKey;
	}

	/**
	 * Set the MD5 hash of this file's content. This should be called whenever the file's blob key is changed (or its associated blob).
	 * @param mD5Hash the new MD5 hash
	 */
	public final void setMD5Hash(final byte[] mD5Hash)
	{
		this.md5Hash = Arrays.copyOf(mD5Hash, mD5Hash.length);
	}

	/**
	 * Set this file's absolute path.
	 * @param path the new absolute path (cannot be {@code null})
	 */
	public final void setPath(final String path)
	{
		//CHECKSTYLE.OFF: MultipleStringLiterals - declaring fields for error messages is silly
		this.path = checkNotNull(path, "Path cannot be null");
		//CHECKSTYLE.ON: MultipleStringLiterals
	}

	/**
	 * Set this file's MIME type.
	 * @param mimeType the new MIME type.
	 */
	public final void setMimeType(final MediaType mimeType)
	{
		this.mimeType = mimeType;
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
		return parent.get();
	}
	
	/**
	 * Set this file's parent.
	 * @param parent the new parent
	 * @see #getParent()
	 */
	public final void setParent(final FileImpl parent)
	{
		checkArgument(parent.namespace.equals(this.namespace));
		this.parent = Ref.create(parent);
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
		return Arrays.copyOf(md5Hash, md5Hash.length);
	}

}
