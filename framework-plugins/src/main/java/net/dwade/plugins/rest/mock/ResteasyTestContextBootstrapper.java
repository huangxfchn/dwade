package net.dwade.plugins.rest.mock;

import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.web.WebTestContextBootstrapper;

public class ResteasyTestContextBootstrapper extends WebTestContextBootstrapper {

	@Override
	protected CacheAwareContextLoaderDelegate getCacheAwareContextLoaderDelegate() {
		return new ResteasyCacheAwareContextLoaderDelegate();
	}

}
