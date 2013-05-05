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
package com.bennavetta.appsite2.filesystem.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.net.MediaType;

/**
 * Mutable helper to store file info while it's being built up. Serializable so that it can be stored
 * in the servlet session across requests for async uploads and callbacks.
 * @author ben
 *
 */
public class FileInfo implements Externalizable
{
	/**
	 * Corresponds to {@link com.bennavetta.appsite2.filesystem.File#getPath()}.
	 */
	private String path;
	
	/**
	 * Corresponds to {@link com.bennavetta.appsite2.filesystem.File#getMimeType()}.
	 */
	private MediaType mimeType;
	
	/**
	 * The blob key identifying the (already uploaded) content of the file.
	 */
	private BlobKey blobKey;
	
	/**
	 * Corresponds to {@link com.bennavetta.appsite2.filesystem.File#getMD5Hash()}.
	 */
	private byte[] md5;
	
	/**
	 * Create a new {@code FileInfo} with all fields set to {@code null}.
	 */
	public FileInfo() {}
	
	/**
	 * Create a new {@code FileInfo} with all fields set to the given values.
	 * @param path the path
	 * @param mimeType the MIME type
	 * @param blobKey the @{code BlobKey}
	 * @param md5 the MD5 hash (will be copied)
	 */
	public FileInfo(final String path, final MediaType mimeType, final BlobKey blobKey, final byte[] md5)
	{
		this.path = path;
		this.mimeType = mimeType;
		this.blobKey = blobKey;
		final byte[] tmp = new byte[md5.length];
		System.arraycopy(md5, 0, tmp, 0, md5.length);
		this.md5 = tmp;
	}


	/**
	 * Get the path that the file will eventually be located at.
	 * @return the path string or {@code null} if not set
	 * @see #setPath(String)
	 * @see com.bennavetta.appsite2.filesystem.File#getPath()
	 */
	public final String getPath()
	{
		return path;
	}

	/**
	 * Set the path that the file will eventually be located at.
	 * @param path the new path string (can be {@code null})
	 * @see #getPath()
	 * @see com.bennavetta.appsite2.filesystem.File#getPath()
	 */
	public final void setPath(final String path)
	{
		this.path = path;
	}

	/**
	 * Get the MIME type of the content that will be stored in the file.
	 * @return a MIME type or {@code null} if not set
	 * @see #setMimeType(MediaType)
	 * @see com.bennavetta.appsite2.filesystem.File#getMimeType()
	 */
	public final MediaType getMimeType()
	{
		return mimeType;
	}
	
	/**
	 * Set the MIME type to use for the file's content.
	 * @param mimeType a MIME type (or {@code null})
	 * @see #getMimeType()
	 * @see com.bennavetta.appsite2.filesystem.File#getMimeType()
	 */
	public final void setMimeType(final MediaType mimeType)
	{
		this.mimeType = mimeType;
	}

	/**
	 * Get the {@link BlobKey} pointing to the file's content.
	 * @return a blob key or {@code null} if one has not been set
	 * @see #setBlobKey(BlobKey)
	 */
	public final BlobKey getBlobKey()
	{
		return blobKey;
	}

	/**
	 * Set the {@link BlobKey} pointing to the file's content.
	 * @param blobKey a blob key or {@code null}
	 * @see #getBlobKey()
	 */
	public final void setBlobKey(final BlobKey blobKey)
	{
		this.blobKey = blobKey;
	}

	/**
	 * Get the MD5 hash of the file's content. This is provided as a field so that any method can
	 * be used to get the hash. For example, if the client is providing the hash, it doesn' make
	 * sense to recalculate it on the server. Modifying the returned array will not affect the one
	 * stored.
	 * @return a byte array containing the hash or {@code null} if not set
	 * @see #setMd5(byte[])
	 * @see com.bennavetta.appsite2.filesystem.File#getMD5Hash()
	 */
	public final byte[] getMd5()
	{
		final byte[] out = new byte[md5.length];
		System.arraycopy(md5, 0, out, 0, md5.length);
		return out;
	}

	/**
	 * Set the MD5 hash of the file's content.
	 * @param md5 a byte array containing the MD5 hash. The array will be copied.
	 * @see #getMd5()
	 * @see com.bennavetta.appsite2.filesystem.File#getMD5Hash()
	 */
	public final void setMd5(final byte[] md5)
	{
		final byte[] tmp = new byte[md5.length];
		System.arraycopy(md5, 0, tmp, 0, md5.length);
		this.md5 = tmp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void writeExternal(final ObjectOutput out) throws IOException
	{
		if(path != null)
		{
			out.writeBoolean(true);
			out.writeUTF(path);
		}
		else
		{
			out.writeBoolean(false);
		}
		
		if(mimeType != null)
		{
			out.writeBoolean(true);
			out.writeUTF(mimeType.toString());
		}
		else
		{
			out.writeBoolean(false);
		}
		
		out.writeObject(blobKey);
		
		if(md5 != null)
		{
			out.writeInt(md5.length);
			out.write(md5);
		}
		else
		{
			out.writeInt(0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException
	{
		if(in.readBoolean())
		{
			this.path = in.readUTF();
		}
		
		if(in.readBoolean())
		{
			this.mimeType = MediaType.parse(in.readUTF());
		}
		
		this.blobKey = (BlobKey) in.readObject();
		
		final int hashLength = in.readInt();
		final byte[] buf = new byte[hashLength];
		in.readFully(buf);
		this.md5 = buf;
	}

}
