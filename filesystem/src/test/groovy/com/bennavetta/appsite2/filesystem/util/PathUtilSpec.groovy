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
package com.bennavetta.appsite2.filesystem.util

import spock.lang.Specification

class PathUtilSpec extends Specification
{
	def "directoryPath rejects null"()
	{
		when:
			PathUtils.directoryPath(null)
		then:
			def e = thrown(NullPointerException)
			e.message == 'Path must not be null'
	}
	
	def "directoryPath doesn't add an extra slash"()
	{
		given:
			String path = '/foo/bar/'
		when:
			path = PathUtils.directoryPath(path)
		then:
			path == '/foo/bar/'
	}
	
	def "directoryPath adds slash when needed"()
	{
		given:
			String path = '/foo/bar'
		when:
			path = PathUtils.directoryPath(path)
		then:
			path == '/foo/bar/'
	}
	
	def "withoutLastComponent rejects null"()
	{
		when:
			PathUtils.withoutLastComponent(null)
		then:
			def e = thrown(NullPointerException)
			e.message == 'Path must not be null'
	}
	
	def "withoutLastPathComponent handles files"()
	{
		given:
			def path = '/foo/bar/baz.txt'
		when:
			path = PathUtils.withoutLastComponent(path)
		then:
			path == '/foo/bar'
	}
	
	def "withoutLastPathComponent handles directories"()
	{
		given:
			def path = '/foo/bar/baz/'
		when:
			path = PathUtils.withoutLastComponent(path)
		then:
			path == '/foo/bar'
	}
	
	def "withoutLastPathComponent handles root"()
	{
		given:
			def path = "/"
		when:
			path = PathUtils.withoutLastComponent(path)
		then:
			path == "/"
	}
	
	def "lastPathComponent rejects null"()
	{
		when:
			PathUtils.lastPathComponent(null)
		then:
			def e = thrown(NullPointerException)
			e.message == "Path must not be null"
	}
	
	// use the examples from [NSString lastPathComponent] (Cocoa API)
	def "lastPathComponent handles NSString examples"()
	{
		expect:
			PathUtils.lastPathComponent(input) == output
		where:
			input << ['/tmp/scratch.tiff', '/tmp/scratch', '/tmp/', 'scratch///', "/"]
			output << ['scratch.tiff', 'scratch', 'tmp', 'scratch', "/"]
	}
	
	def "normailize rejects null"()
	{
		when:
			PathUtils.normalize(null)
		then:
			def e = thrown(NullPointerException)
			e.message == "Path must not be null"
	}
	
	def "normalize behaves as expected"()
	{
		expect:
			PathUtils.normalize(input) == output
		where:
			input << ['/foo/bar/baz', '/test.txt', 'myfile', '/foo/']
			output << ['/foo/bar/baz', '/test.txt', '/myfile', '/foo']
	}
}
