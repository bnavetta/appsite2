package com.bennavetta.appsite2.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * Manage the Appengine service stubs associated with a particular test object.
 * @author ben
 *
 */
public class ServiceManager
{
	/**
	 * Map test objects (instances of Specification, etc.) to {@code ServiceMaanger}s.
	 */
	private static final IdentityHashMap<Object, ServiceManager> HELPERS =
			new IdentityHashMap<>();
	
	/**
	 * The set of services associated with the managed test. 
	 */
	private Set<LocalServiceTestConfig> services = new HashSet<>();
	
	/**
	 * The service helper for the managed test.
	 */
	private LocalServiceTestHelper helper;
	
	/**
	 * Hidden constructor.
	 */
	private ServiceManager() {}
	
	/**
	 * Obtain or create the {@code ServiceManager} for a test object.
	 * @param test the test object
	 * @return a {@code ServiceManager}. Never {@code null}
	 */
	public static final ServiceManager get(Object test)
	{
		if(!HELPERS.containsKey(test))
		{
			HELPERS.put(test, new ServiceManager());
		}
		return HELPERS.get(test);
	}
	
	/**
	 * Get the {@link LocalServiceTestHelper} instance. Once the service test helper has been
	 * created, new services cannot be added.
	 * @return a {@link LocalServiceTestHelper}, created if necessary. Never {@code null}.
	 */
	public LocalServiceTestHelper getHelper()
	{
		if(helper == null)
		{
			helper = new LocalServiceTestHelper(
					services.toArray(new LocalServiceTestConfig[services.size()]));
		}
		return helper;
	}
	
	/**
	 * Add a new service to the set of services available to the test. Once the service test helper
	 * has been created, new services cannot be added.
	 * @param service the service configuration (cannot be {@code null})
	 */
	public void addService(LocalServiceTestConfig service)
	{
		checkState(helper == null, "The test helper has already been created");
		services.add(checkNotNull(service));
	}
	
	/**
	 * Check if a service of the given type is registered.
	 * @param type the service class.
	 * @return {@code true} if an instance of the given service class was found, {@code false} otherwise
	 */
	public boolean hasService(Class<? extends LocalServiceTestConfig> type)
	{
		for(LocalServiceTestConfig service : services)
		{
			if(type.isInstance(service))
			{
				return true;
			}
		}
		return false;
	}
}
