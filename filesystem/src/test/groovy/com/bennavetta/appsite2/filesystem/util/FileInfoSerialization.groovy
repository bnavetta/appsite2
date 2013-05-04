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
}
