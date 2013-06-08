package com.bennavetta.appsite2.test

import spock.lang.Specification

@Category(Specification)
class Appengine
{
	def appengineSetup()
	{
		ServiceManager.get(this).getHelper().setUp()
	}
	
	def appengineTearDown()
	{
		ServiceManager.get(this).getHelper().tearDown()
	}
}
