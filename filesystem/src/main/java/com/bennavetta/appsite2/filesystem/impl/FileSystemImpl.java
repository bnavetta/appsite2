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

import static com.bennavetta.appsite2.filesystem.impl.QFileImpl.fileImpl;

import java.net.URI;

import javax.persistence.EntityManager;

import com.bennavetta.appsite2.filesystem.File;
import com.bennavetta.appsite2.filesystem.FileSystem;
import com.bennavetta.appsite2.filesystem.FileSystemException;
import com.bennavetta.appsite2.filesystem.util.FileInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import com.mysema.query.jpa.impl.JPAQuery;

/**
 * JPA- and BlobStore-based implementation of the file system API.
 * @author ben
 *
 */
public class FileSystemImpl implements FileSystem
{
	/**
	 * The name of the file system.
	 * @see #FileSystemImpl(String)
	 * @see #getName()
	 */
	private final String name;
	
	/**
	 * Create a new file system with the given name. This file system will only consider files in this namespace.
	 * @param name the name of the file system
	 * @see #getName()
	 */
	public FileSystemImpl(final String name)
	{
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getName()
	{
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ImmutableList<? extends File> listFiles(final File directory)
	{
		EntityManager em = null;
		try
		{
			em = FileSystemEMF.get().createEntityManager();
			final JPAQuery query = new JPAQuery(em);
			return ImmutableList.copyOf(query.from(fileImpl)
				.where(fileImpl.parent.eq(directory))
				.list(fileImpl));
		}
		finally
		{
			if(em != null)
			{
				em.close();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final File fileAt(final String path)
	{
		EntityManager em = null;
		try
		{
			em = FileSystemEMF.get().createEntityManager();
			final JPAQuery query = new JPAQuery(em);
			return query.from(fileImpl)
				.where(fileImpl.path.eq(path))
				.uniqueResult(fileImpl);
		}
		finally
		{
			if(em != null)
			{
				em.close();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final File relativeTo(final File base, final String path)
	{
		final String realPath = URI.create(base.getPath()).resolve(path).getPath();
		return fileAt(realPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void delete(final File file, final boolean recurse) throws FileSystemException
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void create(final String path, final MediaType mimeType, final BlobKey blobKey, final byte[] md5) throws FileSystemException
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void create(final FileInfo info) throws FileSystemException
	{
		// TODO Auto-generated method stub
		
	}

}
