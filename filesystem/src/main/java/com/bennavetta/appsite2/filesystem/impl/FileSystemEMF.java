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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Static container for the {@link EntityManagerFactory} that the file system uses internally.
 * @author ben
 *
 */
public class FileSystemEMF
{
	/**
	 * Contains the cached EMF.
	 */
	private static EntityManagerFactory emf;
	
	/**
	 * An object to synchronize on when checking for availability of the EMF.
	 */
	private static final Object emfLock = new Object();
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private FileSystemEMF() {}
	
	/**
	 * Get the {@link EntityManagerFactory}. The EMF will be created if necessary.
	 * @return the initialized EMF. Never {@code null}, but an exception could theoretically occur.
	 */
	public static EntityManagerFactory get()
	{
		synchronized(emfLock) {
			if(emf == null)
			{
				emf = Persistence.createEntityManagerFactory("filesystem");
			}
		}
		return emf;
	}
}
