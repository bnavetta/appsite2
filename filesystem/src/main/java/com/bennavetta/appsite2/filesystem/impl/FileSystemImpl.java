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

import static com.bennavetta.appsite2.filesystem.util.PathUtils.normalize;
import static com.bennavetta.appsite2.filesystem.util.PathUtils.withoutLastComponent;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.googlecode.objectify.ObjectifyService.factory;
import static com.googlecode.objectify.ObjectifyService.ofy;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.bennavetta.appsite2.filesystem.File;
import com.bennavetta.appsite2.filesystem.FileSystem;
import com.bennavetta.appsite2.filesystem.FileSystemException;
import com.bennavetta.appsite2.filesystem.util.FileInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

/**
 * JPA- and BlobStore-based implementation of the file system API.
 * @author ben
 *
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.TooManyMethods"})
public class FileSystemImpl implements FileSystem
{
	/**
	 * The maximum size of the directory listing cache.
	 */
	private static final long MAX_CACHE_SIZE = 1000; // NOPMD - can't really get much shorter
	
	/**
	 * The time to wait after an object in the directory listing cache is last accessed before marking it as expired.
	 */
	private static final long CACHE_EXPIRATION_MINS = 2; // NOPMD - can't really get much shorter
	
	static
	{
		factory().getTranslators().add(new MediaTypeTranslatorFactory());
		factory().register(FileImpl.class);
	}
	
	/**
	 * The name of the file system.
	 * @see #FileSystemImpl(String)
	 * @see #getName()
	 */
	private final String name;
	
	/**
	 * A cache of the contents of directories. Listing directories requires a query, so it is more expensive than a lookup.
	 */
	private final LoadingCache<FileImpl, ImmutableList<Key<FileImpl>>> listingCache;
	
	/**
	 * Create a new file system with the given name. This file system will only consider files in this namespace.
	 * @param name the name of the file system
	 * @see #getName()
	 */
	public FileSystemImpl(final String name)
	{
		this.name = name;
		listingCache = CacheBuilder.newBuilder()
				.maximumSize(MAX_CACHE_SIZE)
				.expireAfterAccess(CACHE_EXPIRATION_MINS, TimeUnit.MINUTES)
				.build(new FileListingLoader());
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
	public final ImmutableList<? extends File> listFiles(final File directory) throws FileSystemException
	{
		//CHECKSTYLE.OFF: MultipleStringLiterals - declaring fields for error messages is silly
		checkArgument(directory instanceof FileImpl, "Unsupported File implementation: %s", directory);
		//CHECKSTYLE.ON: MultipleStringLiterals
		try
		{
			return ImmutableList.copyOf(ofy().load().keys(listingCache.get((FileImpl) directory)).values());
		}
		catch (final ExecutionException e)
		{
			//CHECKSTYLE.OFF: MultipleStringLiterals - more unreadable if I use a constant
			throw new FileSystemException("Error listing directory " + directory.getPath(), e);
			//CHECKSTYLE.ON: MultipleStringLiterals
		}
	}
	
	@Override
	public final ImmutableList<String> list(final File directory) throws FileSystemException
	{
		//CHECKSTYLE.OFF: MultipleStringLiterals - declaring fields for error messages is silly
		checkArgument(directory instanceof FileImpl, "Unsupported File implementation: %s", directory);
		//CHECKSTYLE.ON: MultipleStringLiterals
		try
		{
			return new ImmutableList.Builder<String>().addAll(FluentIterable.from(
				listingCache.get((FileImpl) directory)).transform(new KeyToString())).build();
		}
		catch(final ExecutionException e)
		{
			//CHECKSTYLE.OFF: MultipleStringLiterals - more unreadable if I use a constant
			throw new FileSystemException("Error listing directory " + directory.getPath(), e);
			//CHECKSTYLE.ON: MultipleStringLiterals
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final File fileAt(final String path)
	{
		return ofy().load().type(FileImpl.class).id(normalize(path)).now();
	}
	
	@Override
	public final ImmutableList<? extends File> filesAt(final String... paths)
	{
		return filesAt(Arrays.asList(paths));
	}

	@Override
	public final ImmutableList<? extends File> filesAt(final Iterable<String> paths)
	{
		final Iterable<String> normalized = FluentIterable.from(paths).transform(new Normalizer());
		return ImmutableList.copyOf(ofy().load().type(FileImpl.class).ids(normalized).values());
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
		//CHECKSTYLE.OFF: MultipleStringLiterals - declaring fields for error messages is silly
		checkArgument(file instanceof FileImpl, "Unsupported File implementation: %s", file);
		//CHECKSTYLE.ON: MultipleStringLiterals
		if(recurse)
		{
			final Set<FileImpl> filesToDelete = listRecurse((FileImpl) file);
			ofy().transact(new VoidWork()
			{
				/**
				 * Run in a transaction so that the file tree will be deleted once and for all, or not at all.
				 */
				@Override
				public void vrun()
				{
					ofy().delete().entities(filesToDelete);
				}
			});
		}
		else
		{
			ofy().delete().entity(file);
		}
	}

	/**
	 * Internal method to do a recursive directory listing.
	 * @param file the root file to recurse from
	 * @throws FileSystemException if there is an error listing the files
	 * @return the list of files located under the given directory
	 */
	private final Set<FileImpl> listRecurse(final FileImpl file) throws FileSystemException
	{
		final Set<FileImpl> files = new HashSet<>();
		files.add(file);
		if(file.isDirectory())
		{
			for(File child : listFiles(file))
			{
				files.addAll(listRecurse((FileImpl) child));
			}
		}
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final File create(final String path, final MediaType mimeType, final BlobKey blobKey, final byte[] md5) throws FileSystemException
	{
		final String normalized = normalize(checkNotNull(path, "Path of file cannot be null"));
		final FileImpl parent = directory(withoutLastComponent(normalized));
		final FileImpl file = new FileImpl(normalized, parent, name);
		file.setMimeType(checkNotNull(mimeType, "MIME type cannot be null"));
		file.setBlobKey(checkNotNull(blobKey, "Blob key cannot be null"));
		file.setMD5Hash(checkNotNull(md5, "MD5 hash cannot be null"));
		ofy().save().entity(file);
		return file;
	}

	/**
	 * Locate a directory. The directory will be created if necessary.
	 * @param path the path of the directory
	 * @return the loaded (possibly created) directory
	 * @throws IllegalStateException if the directory exists but is not a directory
	 */
	private FileImpl directory(final String path) throws IllegalStateException
	{
		FileImpl dir = (FileImpl) fileAt(path);
		if(dir == null)
		{
			final FileImpl parent = directory(withoutLastComponent(path));
			dir = new FileImpl(path, parent, name);
			ofy().save().entity(dir);
			return dir;
		}
		else
		{
			checkState(dir.isDirectory(), "File %s is not a directory", dir.getPath());
			return dir;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final File create(final FileInfo info) throws FileSystemException
	{
		return create(info.getPath(), info.getMimeType(), info.getBlobKey(), info.getMd5());
	}
	
	/**
	 * A {@link Function} that passes its input through {@link com.bennavetta.appsite2.filesystem.util.PathUtils#normalize(String)}.
	 * @author ben
	 */
	private static final class Normalizer implements Function<String, String>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String apply(final String input)
		{
			return normalize(input);
		}	
	}
	
	/**
	 * A {@link CacheLoader} that loads values by performing a keys-only query that loads {@link FileImpl}s whose parent is
	 * the given file (the key).
	 * @author ben
	 *
	 */
	private static final class FileListingLoader extends CacheLoader<File, ImmutableList<Key<FileImpl>>>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public ImmutableList<Key<FileImpl>> load(final File key)
		{
			return ImmutableList.copyOf(ofy().load().type(FileImpl.class).filter("parent =", key).keys());
		}
		
	}
	
	/**
	 * A {@link Function} that transforms a {@link Key} to a {@link String} by calling {@link Key#getString()}. 
	 * @author ben
	 */
	private static final class KeyToString implements Function<Key<FileImpl>, String>
	{
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String apply(final Key<FileImpl> input)
		{
			return input.getString();
		}	
	}
}
