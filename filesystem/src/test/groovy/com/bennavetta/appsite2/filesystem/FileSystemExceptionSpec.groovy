package com.bennavetta.appsite2.filesystem

import spock.lang.Specification

class FileSystemExceptionSpec extends Specification
{
	def "exception is throwable"()
	{
		when:
			throw new FileSystemException('Hello, World!')
		then:
			def e = thrown(FileSystemException)
			e.cause == null;
			e.message == 'Hello, World!'
	}
	
	def "exception takes cause"()
	{
		when:
			throw new FileSystemException(new Exception('Testing'))
		then:
			def e = thrown(FileSystemException)
			e.cause != null
			e.cause.message == 'Testing'
	}
	
	def "exception takes cause and message"()
	{
		when:
			throw new FileSystemException('Hello, World!', new Exception('Testing'))
		then:
			def e = thrown(FileSystemException)
			e.cause != null
			e.cause.message == 'Testing'
			e.message == 'Hello, World!'
	}
}
