package com.bennavetta.appsite2.filesystem.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.appengine.repackaged.com.google.common.base.Joiner;
import com.google.appengine.repackaged.com.google.common.base.Splitter;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;

/**
 * Utility methods for manipulating paths.
 * @author ben
 *
 */
public class PathUtils
{
	public static final String SEPARATOR = "/";
	
	private PathUtils() {}
	
	/**
	 * Ensure that a directory path ends with a trailing slash.
	 * @param path the path string (cannot be {@code null})
	 * @return the path, ending with a slash
	 */
	public static final String directoryPath(String path)
	{
		checkNotNull(path, "Path must not be null");
		return path.endsWith(SEPARATOR) ? path : path.concat(SEPARATOR);
	}
	
	/**
	 * Get the last component in a path string. For example, the last path component of {@code /foo/bar/baz.txt} is {@code baz.txt}. 
	 * @param path the path string (cannot be {@code null})
	 * @return the last component in the path
	 */
	public static final String lastPathComponent(String path)
	{
		return Iterables.getLast(Splitter.on(SEPARATOR).split(checkNotNull(path, "Path must not be null")));
	}
	
	/**
	 * Remove the last component of a path. For example, this method would return {@code /foo/bar} given {@code /foo/bar/baz.txt}.
	 * @param path the path string (cannot be {@code null})
	 * @return the path minus its last component
	 */
	public static final String withoutLastComponent(String path)
	{
		List<String> components = Splitter.on(SEPARATOR).splitToList(checkNotNull(path, "Path cannot be null"));
		return Joiner.on(SEPARATOR).join(components.subList(0, components.size()));
	}
}
