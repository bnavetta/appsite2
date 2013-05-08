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
package com.bennavetta.appsite2.filesystem;

import com.bennavetta.appsite2.filesystem.util.FileInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

/**
 * The core abstraction of the file system API, a {@code FileSystem} provides support for most file operations. This includes creating
 * and deleting files, as well as read operations. This file system API has no concept of a working directory, as multiple requests may
 * be using a file system simultaneously.
 * @author ben
 */
public interface FileSystem
{
	/**
	 * Get the unique name of this file system. Each file system has an unique name tied to its location in the Datastore.
	 * @return this filesystem's name string, which must never be {@code null}
	 */
	public String getName();
	
	/**
	 * List the files located within a particular directory. This listing is not recursive, and ordering is not guaranteed.
	 * @param directory the directory to be listed. The directory argument must not be {@code null}, and it must be a directory.
	 * @return a collection of {@code File}s located under the given directory. Never {@code null}.
	 * @see File#isDirectory()
	 */
	public ImmutableList<? extends File> listFiles(File directory);
	
	/**
	 * Return the file located at the given absolute path.
	 * @param path the path of the file (cannot be null)
	 * @return a {@code File}, or {@code null} if the file does not exist
	 */
	public File fileAt(String path);
	
	/**
	 * Resolve a relative path to a file against a base. The rules for resolving are the same as those for {@link java.net.URI#resolve(java.net.URI)}.
	 * For example, given a base of {@code /foo/bar} and a path of {@code baz}, the returned file would be at {@code /foo/baz}. Given a 
	 * base of {@code /foo/bar/} (note the trailing slash) and a path of {@code baz}, the returned file would be at {@code /foo/bar/baz}.
	 * @param base the base file to resolve against. Cannot be {@code null}.
	 * @param path the relative path of the file. Cannot be {@code null}.
	 * @return a {@code File} at the resolved path, or {@code null} if the file at the resolved path does not exist.
	 */
	public File relativeTo(File base, String path);
	
	/**
	 * Delete a file or directory.
	 * @param file the file or directory to delete
	 * @param recurse if {@code true} and the file is a directory, then all children of this file will
	 * also be deleted
	 * @throws FileSystemException if there is a problem deleting the file(s)
	 */
	public void delete(File file, boolean recurse) throws FileSystemException;
	
	/**
	 * Create a new file. None of the parameters to this method can be {@code null} because all
	 * are necessary fields for {@link File} objects, and the file system will not work properly
	 * without them. The file's parent will be determined from its path. This method should not
	 * be used to create directories, which are created as needed.
	 * @param path the absolute path to the file. See {@link File#getPath()}
	 * @param mimeType the MIME type of the file.
	 * @param blobKey the identifier of the blob containing the file's content.
	 * @param md5 the MD5 hash of the file (provided here so it can be generated more efficiently by callers).
	 * @throws FileSystemException if there is a problem creating the file.
	 */
	public void create(String path, MediaType mimeType, BlobKey blobKey, byte[] md5) throws FileSystemException;
	
	/**
	 * Create a new file using the given {@link FileInfo}. All fields of the {@code FileInfo} object
	 * must be set.
	 * @param info the information to use creating the file.
	 * @throws FileSystemException if there is a problem creating the file.
	 * @see #create(String, MediaType, BlobKey, byte[])
	 */
	public void create(FileInfo info) throws FileSystemException;
}
