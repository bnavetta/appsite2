package com.bennavetta.appsite2.test

import spock.lang.Specification

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig

@Category(Specification)
class Datastore
{
	def datastore()
	{
		ServiceManager.get(this).addService(new LocalDatastoreServiceTestConfig())
	}
	
	def datastore(Closure conf)
	{
		LocalDatastoreServiceTestConfig service = new LocalDatastoreServiceTestConfig()
		service.with(conf)
		ServiceManager.get(this).addService(service)
	}
}
