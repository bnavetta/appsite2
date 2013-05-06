package com.bennavetta.appsite2.filesystem.impl

import spock.lang.Specification

class EMFSpec extends Specification
{
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
			def first, second
		when:
			first = FileSystemEMF.get()
			second = FileSystemEMF.get()
		then:
			first != null
			first is second
	}
}
