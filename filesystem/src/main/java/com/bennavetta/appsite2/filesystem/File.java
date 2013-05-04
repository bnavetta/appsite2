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

import com.google.common.net.MediaType;

/**
 * A {@code File} object represents an existing file on the virtual file system. The {@link FileSystem} associated with this file must
 * be used for all read/write operations. The forward slash ('/') character is the only valid path separator.
 * @author ben
 *
 */
public interface File
{
	/**
	 * Returns the name of this file. The name of a file is everything following the last path component.
	 * @return a file name. Never {@code null}.
	 */
	public String getName();
	
	/**
	 * Returns the absolute path to this file. This path is only valid for the file system to which this file belongs.
	 * @return the absolute path uniquely identifying this file. Never {@code null}.
	 */
	public String getPath();
	
	/**
	 * Returns the {@link FileSystem} that this file belongs to.
	 * @return a {@code FileSystem}, never {@code null}
	 */
	public FileSystem getFileSystem();
	
	/**
	 * Get the parent of this file. A file's parent is the file located one path segment "above" the file. For example @{code /foo/} is
	 * the parent file of {@code /foo/bar}.
	 * @return the file's parent. Never {@code null}
	 */
	public File getParent();
	
	/**
	 * Check to see if this file is a directory. A directory file should not contain content, but it can contain other files. The contents
	 * of a directory can be listed by {@link FileSystem#listFiles(File)}.
	 * @return {@code true} if this file is a directory, {@code false} otherwise
	 */
	public boolean isDirectory();
	
	/**
	 * Get the type of the content stored in this file. The content type of a file is retained when
	 * uploaded so that the file can be served later on.
	 * @return the mime type (never {@code null})
	 */
	public MediaType getMimeType();
	
	/**
	 * Get the MD5 hash of this file's content. The hash is stored upon file creation, so there is no
	 * penalty for repeatedly invoking this method. The byte array should not be modified, but if it
	 * is, it should not affect the file's actual hash.
	 * @return a byte array containing the hash of the file.
	 */
	public byte[] getMD5Hash();
}
