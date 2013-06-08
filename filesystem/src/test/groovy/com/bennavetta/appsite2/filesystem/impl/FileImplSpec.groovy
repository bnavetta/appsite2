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
package com.bennavetta.appsite2.filesystem.impl

import spock.lang.Specification

class FileImplSpec extends Specification
{
	def "hash is copied"()
	{
		given:
			FileImpl file = new FileImpl();
			byte[] bytes = [1, 2, 3, 4] as byte[]
		when:
			file.setMD5Hash(bytes)
		then:
			!bytes.is(file.@md5Hash) // check for setter copy
			bytes == file.getMD5Hash() // check for equality
			!file.@md5Hash.is(file.getMD5Hash()) // check for getter copy
	}
	
	def "constructor rejects null path"()
	{
		when:
			new FileImpl(null, null, "ns")
		then:
			def e = thrown(NullPointerException)
			e.message == "Path cannot be null"
	}
	
	def "constructor rejects null namespace"()
	{
		when:
			new FileImpl("foo", null, null)
		then:
			def e = thrown(NullPointerException)
			e.message == "Namespace cannot be null"
	}
	
	def "setNamespace rejects null"()
	{
		when:
			new FileImpl().namespace = null
		then:
			def e = thrown(NullPointerException)
			e.message == "Namespace cannot be null"
	}
	
	def "setPath rejects null path"()
	{
		when:
			new FileImpl().path = null
		then:
			def e = thrown(NullPointerException)
			e.message == "Path cannot be null"
	}
}
