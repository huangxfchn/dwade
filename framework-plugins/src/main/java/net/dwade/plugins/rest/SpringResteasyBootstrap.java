package net.dwade.plugins.rest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderListener;
import org.jboss.resteasy.plugins.spring.SpringContextLoaderSupport;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

/**
 * 集成Spring和Resteasy
 * @author huangxf
 * @date 2017年4月21日
 */
public class SpringResteasyBootstrap extends ContextLoaderListener implements ServletContextListener {
	
	public static final String RESTEASY_INJECTOR = "resteasy.injector.factory";
	
	public static final String RESTEASY_PROVIDERS = "resteasy.providers";

	protected ResteasyDeployment deployment;
	
	private SpringContextLoaderSupport springContextLoaderSupport = new SpringContextLoaderSupport();
	
	private static final PaymentInjectorFactory injectorFactory = new PaymentInjectorFactory();
	
	private static final Logger logger = LoggerFactory.getLogger( SpringResteasyBootstrap.class );
	
	public void contextInitialized( ServletContextEvent event ) {
		
		ServletContext servletContext = event.getServletContext();
		
		//检查扫描参数
		this.checkResteasyScan( servletContext );

		//初始化Resteasy，因为在SpringContextLoaderSupport初始化的时候需要ResteasyDeployment实例
		ListenerBootstrap config = new ListenerBootstrap( servletContext );
		deployment = config.createDeployment();
		deployment.start();
		
		//deployment.getProviderFactory().setInjectorFactory( new PaymentInjectorFactory() );
		//servletContext.setAttribute( ResteasyDeployment.class.getName(), deployment );
		
		//如果未指定injectorFactory则使用自定义的PaymentInjectorFactory
		String injectorParam = servletContext.getInitParameter( RESTEASY_INJECTOR );
		String providers = servletContext.getInitParameter(RESTEASY_PROVIDERS);
		
		if (providers == null) {
			//硬编码添加异常处理Hanlder，也可以使用spi
			deployment.getProviderFactory().register( new PaymentExceptionHandler() );
		}
		
		if ( injectorParam == null ) {

			//加入自定义的InjectorFactory
			deployment.getProviderFactory().setInjectorFactory( injectorFactory );
			logger.info( "Set InjectorFactory with {}", injectorFactory.getClass() );
		}
		
		servletContext.setAttribute( ResteasyDeployment.class.getName(), deployment );
		
		//初始化spring容器，初始化时会调用到customizeContext方法将SpringBeanProcessor添加到spring容器中
		super.contextInitialized( event );
		
	}
	
	/**
	 * 主要目的是将SpringBeanProcessor注册到Spring容器中
	 * @see SpringContextLoaderListener
	 */
	@Override
	protected void customizeContext( ServletContext servletContext, ConfigurableWebApplicationContext webContext ) {
		super.customizeContext( servletContext, webContext );
		this.springContextLoaderSupport.customizeContext( servletContext, webContext );
		webContext.addApplicationListener( injectorFactory );
	}

	/**
	 * Resteasy在集成Spring的时候不允许使用扫描参数resteasy.scan, resteasy.scan.resources, resteasy.scan.providers
	 * @see SpringContextLoaderListener#contextInitialized
	 */
	protected void checkResteasyScan( ServletContext context ) throws RuntimeException {
		
		boolean scanProviders = false;
		boolean scanResources = false;

		String sProviders = context.getInitParameter("resteasy.scan.providers");
		if (sProviders != null)	{
			scanProviders = Boolean.valueOf(sProviders.trim());
		}
		String scanAll = context.getInitParameter("resteasy.scan");
		if (scanAll != null) {
			boolean tmp = Boolean.valueOf(scanAll.trim());
			scanProviders = tmp || scanProviders;
			scanResources = tmp;
		}
		String sResources = context.getInitParameter("resteasy.scan.resources");
		if (sResources != null)	{
			scanResources = Boolean.valueOf(sResources.trim());
		}
		if (scanProviders || scanResources)	{
			throw new RuntimeException("You cannot use resteasy.scan, resteasy.scan.resources, "
					+ "or resteasy.scan.providers with the SpringContextLoaderLister as this may cause serious deployment errors in your application");
		}
	}
	
	public void contextDestroyed(ServletContextEvent event)	{
		deployment.stop();
	}


}
