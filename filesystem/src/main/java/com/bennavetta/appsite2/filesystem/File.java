package com.bennavetta.appsite2.filesystem;

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
}
