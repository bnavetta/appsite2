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

import com.bennavetta.appsite2.filesystem.FileSystemManager
import com.bennavetta.appsite2.test.Appengine
import com.bennavetta.appsite2.test.Objectify

@Mixin([Appengine, Objectify])
class FileImplSpec extends Specification
{
	def setupSpec()
	{
		objectify(FileImpl.class)
	}
	
	def setup()
	{
		appengineSetup()
	}
	
	def cleanup()
	{
		appengineTearDown()
	}
	
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
		given:
			def file
		when:
			file = new FileImpl("foo", null, null)
		then:
			def e = thrown(NullPointerException)
			e.message == "Namespace cannot be null"
	}
	
	def "constructor adds parent"()
	{
		given:
			def parent = new FileImpl("/foo", null, "myfs")
			ofy().save().entity(parent).now()
			def child
		when:
			child = new FileImpl("/foo/bar", parent, "myfs")
		then:
			child.getParent() == parent
	}
	
	def "parent namespace must match child's"()
	{
		given:
			def parent = new FileImpl("/foo", null, "myfs1")
			ofy().save().entity(parent).now() // so ref works
			def child = null
		when:
			child = new FileImpl("/foo/bar", parent, "myfs")
		then:
			thrown(IllegalArgumentException)
	}
	
	def "setNamespace rejects null"()
	{
		when:
			new FileImpl().setNamespace(null)
		then:
			def e = thrown(NullPointerException)
			e.message == "Namespace cannot be null"
	}
	
	def "setNamespace works"()
	{
		given:
			def file = new FileImpl()
		when:
			file.setNamespace("foo")
		then:
			file.getNamespace() == "foo"
	}
	
	def "setPath rejects null path"()
	{
		when:
			new FileImpl().setPath(null)
		then:
			def e = thrown(NullPointerException)
			e.message == "Path cannot be null"
	}
	
	def "getFileSystem works"()
	{
		given:
			FileImpl file = new FileImpl("/foo", null, "myfs")
		when:
			FileSystemManager.get("myfs")
		then:
			file.fileSystem != null
			file.fileSystem.name == "myfs"
	}
}
