package net.dwade.plugins.resteasy.mock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.dwade.plugins.resteasy.SpringResteasyBootstrap;

/**
 * 参考AbstractGenericWebContextLoader.loadContext的方法，初始化MockServletContext
 * 创建Listener
 * @author huangxf
 * @date 2017年4月30日
 */
public class ResteasyCacheAwareContextLoaderDelegate extends
		DefaultCacheAwareContextLoaderDelegate {
	
	private ServletContext servletContext;

	/**
	 * 由于父类中首先从cache中获取ApplicationContext，取不到才会调用loadContextIntername方法获取ApplicationContext
	 */
	@Override
	protected ApplicationContext loadContextInternal(
			MergedContextConfiguration mergedConfig)
			throws Exception {
		
		if (!(mergedConfig instanceof WebMergedContextConfiguration)) {
			throw new IllegalArgumentException(String.format(
				"Cannot load WebApplicationContext from non-web merged context configuration %s. "
						+ "Consider annotating your test class with @WebAppConfiguration.", mergedConfig));
		}
		WebMergedContextConfiguration webMergedConfig = (WebMergedContextConfiguration) mergedConfig;
		
		String resourceBasePath = webMergedConfig.getResourceBasePath();
		ResourceLoader resourceLoader = resourceBasePath.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX) ? new DefaultResourceLoader()
				: new FileSystemResourceLoader();

		this.servletContext = new MockServletContext( resourceBasePath, resourceLoader );
		
		//将Spring的xml配置转换成web.xml中的contextConfigLocation参数
		StringBuilder locations = new StringBuilder();
		for ( String location : webMergedConfig.getLocations() ) {
			locations.append( location ).append( "," );
		}
		locations.deleteCharAt( locations.length() - 1 );
		servletContext.setInitParameter( "contextConfigLocation", locations.toString() );
		
		//初始化ServletListener
		ServletContextListener bootstrapListener = new SpringResteasyBootstrap();
		ServletContextEvent event = new ServletContextEvent( servletContext );
		bootstrapListener.contextInitialized( event );
		
		//存放在上下文中
		servletContext.setAttribute( SpringResteasyBootstrap.class.getName(), bootstrapListener );
		
		return WebApplicationContextUtils.getWebApplicationContext( servletContext );
		
	}

	@Override
	public void closeContext(
			MergedContextConfiguration mergedContextConfiguration,
			HierarchyMode hierarchyMode) {
		ServletContextListener listener = (ServletContextListener)servletContext.getAttribute( SpringResteasyBootstrap.class.getName() );
		listener.contextDestroyed( new ServletContextEvent( servletContext ) );
		super.closeContext(mergedContextConfiguration, hierarchyMode);
	}
	
}
