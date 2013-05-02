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

/**
 * The base exception type for the file system API.
 * This exception can be used to wrap {@link java.io.IOException}s as well.
 * @author ben
 *
 */
public class FileSystemException extends Exception
{
	/**
	 * The serial version UID. This class probably shouldn't be serialized, though.
	 */
	private static final long serialVersionUID = 5941954262822209489L;

	/**
	 * Create a {@code FileSystemException} with a detail message and a cause.
	 * @param message the error message
	 * @param cause the {@code Throwable} that caused this exception
	 */
	public FileSystemException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * Create a {@code FileSystemException} with a detail message and no cause.
	 * @param message the error message
	 */
	public FileSystemException(final String message)
	{
		super(message);
	}

	/**
	 * Create a {@code FileSystemException} with a cause but no detail message.
	 * @param cause the {@code Throwable} that caused this exception
	 */
	public FileSystemException(final Throwable cause)
	{
		super(cause);
	}
}
