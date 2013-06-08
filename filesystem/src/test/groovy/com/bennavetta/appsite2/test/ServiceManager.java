package com.bennavetta.appsite2.test;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ServiceManager
{
	private static final IdentityHashMap<Object, ServiceManager> HELPERS =
			new IdentityHashMap<>();
	
	private Set<LocalServiceTestConfig> services = new HashSet<>();
	private LocalServiceTestHelper helper;
	
	private ServiceManager() {}
	
	public static final ServiceManager get(Object test)
	{
		if(!HELPERS.containsKey(test))
		{
			HELPERS.put(test, new ServiceManager());
		}
		return HELPERS.get(test);
	}
	
	public LocalServiceTestHelper getHelper()
	{
		if(helper == null)
		{
			helper = new LocalServiceTestHelper(
					services.toArray(new LocalServiceTestConfig[services.size()]));
		}
		return helper;
	}
	
	public void addService(LocalServiceTestConfig service)
	{
		services.add(service);
	}
	
	public boolean hasService(Class<? extends LocalServiceTestConfig> type)
	{
		for(LocalServiceTestConfig service : services)
		{
			if(type.isInstance(service))
				return true;
		}
		return false;
	}
}
