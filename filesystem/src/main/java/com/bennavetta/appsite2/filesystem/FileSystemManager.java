package com.bennavetta.appsite2.filesystem;

import java.util.concurrent.ConcurrentHashMap;

import com.bennavetta.appsite2.filesystem.impl.FileSystemImpl;

/**
 * A manager class to keep track of the file systems. The file system API supports more than
 * one file system, so this class keeps track of all the ones that have been created and caches them.
 * @author ben
 *
 */
public final class FileSystemManager
{	
	/**
	 * All the file systems.
	 */
	private static final ConcurrentHashMap<String, FileSystem> FILE_SYSTEMS = new ConcurrentHashMap<>();
	
	/**
	 * Hidden constructor.
	 */
	private FileSystemManager() {}
	
	/**
	 * Get a file system by name.
	 * @param name the name of the file system.
	 * @return the file system. Never {@code null}.
	 */
	public static FileSystem get(final String name)
	{
		if(!FILE_SYSTEMS.containsKey(name))
		{
			FILE_SYSTEMS.put(name, new FileSystemImpl(name));
		}
		return FILE_SYSTEMS.get(name);
	}
}
