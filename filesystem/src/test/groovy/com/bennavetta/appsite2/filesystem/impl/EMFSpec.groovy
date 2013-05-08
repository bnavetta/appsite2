package com.bennavetta.appsite2.filesystem.impl

import spock.lang.Specification

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig
import com.google.appengine.tools.development.testing.LocalServiceTestHelper

class EMFSpec extends Specification
{
	final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig())
	
	def setup()
	{
		helper.setUp()
	}
	
	def cleanup()
	{
		helper.tearDown()
	}
	
	def "lock is initialized"()
	{
		when:
			FileSystemEMF.emfLock
		then:
			FileSystemEMF.emfLock != null
	}
	
	def "EMF is cached"()
	{
		given:
			def first
			def second
		when:
			first = FileSystemEMF.get()
			second = FileSystemEMF.get()
		then:
			first != null
			first.is second
	}
}
