package com.bennavetta.appsite2.filesystem.impl;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class FileSystemEMF
{
	private static EntityManagerFactory emf;
	
	private FileSystemEMF() {}
	
	public static EntityManagerFactory get()
	{
		synchronized(emf) {
			if(emf == null)
			{
				emf = Persistence.createEntityManagerFactory("filesystem");
			}
		}
		return emf;
	}
}
