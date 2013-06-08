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
package com.bennavetta.appsite2.test

import spock.lang.Specification

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig

@Category(Specification)
class Datastore
{
	def datastore()
	{
		ServiceManager.get(this).addService(new LocalDatastoreServiceTestConfig())
	}
	
	def datastore(Closure conf)
	{
		LocalDatastoreServiceTestConfig service = new LocalDatastoreServiceTestConfig()
		service.with(conf)
		ServiceManager.get(this).addService(service)
	}
}
