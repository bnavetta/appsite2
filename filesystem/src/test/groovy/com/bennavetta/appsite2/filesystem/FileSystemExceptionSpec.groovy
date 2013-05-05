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
