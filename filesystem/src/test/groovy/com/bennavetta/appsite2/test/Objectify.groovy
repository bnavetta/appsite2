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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;

import spock.lang.Specification

@Category(Specification)
class Objectify
{	
	def objectify(Class<?>... classes)
	{
		if(!ServiceManager.get(this).hasService(LocalDatastoreServiceTestConfig))
		{
			ServiceManager.get(this).addService(new LocalDatastoreServiceTestConfig());
		}
		
		classes.each { clazz ->
			ObjectifyService.register(clazz)
		}
	}
	
	com.googlecode.objectify.Objectify ofy()
	{
		return ObjectifyService.ofy()
	}
}
