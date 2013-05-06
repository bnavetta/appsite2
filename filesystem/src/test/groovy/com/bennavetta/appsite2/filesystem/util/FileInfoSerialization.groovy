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

import com.google.appengine.api.blobstore.BlobKey
import com.google.common.net.MediaType

class FileInfoSerialization extends Specification
{
	def "can serialize all fields"()
	{
		given:
			def info = new FileInfo()
			info.blobKey = new BlobKey("1234")
			info.md5 = new byte[0]
			info.mimeType = MediaType.PLAIN_TEXT_UTF_8
			info.path = '/foo/bar'
			def read = null
		when:
			def data = new ByteArrayOutputStream()
			new ObjectOutputStream(data).writeObject(info)
			read = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray())).readObject()
		then:
			read instanceof FileInfo
			read.blobKey == info.blobKey
			read.md5 == info.md5
			read.mimeType == info.mimeType
			read.path == info.path
	}
	
	def "can have null fields"()
	{
		given:
			def info = new FileInfo()
			def read = null
		when:
			def data = new ByteArrayOutputStream()
			new ObjectOutputStream(data).writeObject(info)
			read = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray())).readObject()
		then:
			read instanceof FileInfo
			read.blobKey == null
			read.md5 != null
			read.md5.length == 0
			read.mimeType == null
			read.path == null
	}
	
	def "can create FileInfo"()
	{
		given:
			def info
		when:
			info = new FileInfo("/foo/bar", MediaType.ZIP, new BlobKey("1234"), new byte[0])
		then:
			info.path == '/foo/bar'
			info.mimeType == MediaType.ZIP
			info.blobKey.keyString == '1234'
			info.md5.size() == 0
	}
}
