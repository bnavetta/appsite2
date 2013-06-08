package com.bennavetta.appsite2.test

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

import spock.lang.Specification

@Category(Specification)
class Objectify
{	
	def objectify(Class<?>... classes)
	{
		if(!ServiceManager.get(this).hasService(LocalDatastoreServiceTestConfig))
		{
			ServiceManager.get(this).addService(new LocalDatastoreServiceTestConfig());
		}
		
		classes.each { clazz ->
			ObjectifyService.register(clazz)
		}
	}
	
	com.googlecode.objectify.Objectify ofy()
	{
		return ObjectifyService.ofy()
	}
}
