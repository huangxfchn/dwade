package net.dwade.plugins.spring.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import net.dwade.annotation.CheckSign;
import net.dwade.annotation.RequestDataBody;
import net.dwade.annotation.SignResponseBody;
import net.dwade.core.SessionUser;
import net.dwade.core.sign.Sign;

/**
 * 使用{@link BeanPostProcessor}对SpringMVC进行扩展，
 * 支持{@link RequestDataBody}、{@link Sign}、{@link SessionUser}、{@link CheckSign}、{@link SignResponseBody}
 * @see RequestMappingHandlerAdapter
 * @see BeanPostProcessor
 * @author huangxf
 * @date 2017年4月13日
 */
public class PaymentControllerSupport implements BeanFactoryPostProcessor, BeanPostProcessor, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
	@SuppressWarnings("unchecked")
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		BeanDefinition definition = beanFactory.getBeanDefinition( RequestMappingHandlerMapping.class.getName() );
		Object value = definition.getPropertyValues().get( "interceptors" );
		
		// 初始化CheckSignInterceptorProcessor
		HandlerInterceptor interceptor = new CheckSignInterceptorProcessor();
		applicationContext.getAutowireCapableBeanFactory().initializeBean( interceptor, 
				CheckSignInterceptorProcessor.class.getName() );
		
		// 使用编码的方式往RequestMappingHandlerMapping添加拦截器
		if ( value == null ) {
			List<HandlerInterceptor> interceptors = new ArrayList<HandlerInterceptor>();
			interceptors.add( interceptor );
			definition.getPropertyValues().add( "interceptors", interceptors );
		} else {
			List<HandlerInterceptor> interceptors = (List<HandlerInterceptor>)value;
			interceptors.add( interceptor );
		}
		
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		if ( bean instanceof RequestMappingHandlerAdapter ) {
			RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter)bean;
			registerArgumentsResolvers( adapter );
			registerReturnValueHandlers( adapter );
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
	
	/**
	 * 注册参数解析器 
	 * @param adapter
	 * @return void
	 */
	private void registerArgumentsResolvers( RequestMappingHandlerAdapter adapter ) {
		List<HandlerMethodArgumentResolver> resolvers = this.getCustomerArgumentResolvers( adapter );
		if ( adapter.getCustomArgumentResolvers() == null ) {
			adapter.setCustomArgumentResolvers( resolvers );
		} else {
			adapter.getCustomArgumentResolvers().addAll( resolvers );
		}
	}
	
	/**
	 * 注册返回值处理器
	 * @param adapter
	 * @return void
	 */
	private void registerReturnValueHandlers( RequestMappingHandlerAdapter adapter ) {
		List<HandlerMethodReturnValueHandler> resolvers = this.getCustomerReturnValueHandler( adapter );
		if ( adapter.getCustomReturnValueHandlers() == null ) {
			adapter.setCustomReturnValueHandlers( resolvers );
		} else {
			adapter.getCustomReturnValueHandlers().addAll( resolvers );
		}
	}
	
	protected List<HandlerMethodArgumentResolver> getCustomerArgumentResolvers( RequestMappingHandlerAdapter adapter ) {
		
		//处理method参数中SessionUser
		HandlerMethodArgumentResolver sessionUserResolver = new SessionUserResolver();
		applicationContext.getAutowireCapableBeanFactory().initializeBean( sessionUserResolver, SessionUserResolver.class.getName() );
		
		//对请求参数进行处理，解析data节点、签名
		HandlerMethodArgumentResolver requestDataResolver = new RequestDataConverterProcessor( adapter.getMessageConverters() );
		applicationContext.getAutowireCapableBeanFactory().initializeBean( requestDataResolver, RequestDataConverterProcessor.class.getName() );
		
		//处理验签
		HandlerMethodArgumentResolver signResolver = new SignResponseProcessor( adapter.getMessageConverters() );
		applicationContext.getAutowireCapableBeanFactory().initializeBean( signResolver, SignResponseProcessor.class.getName() );
		
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		resolvers.add( sessionUserResolver );
		resolvers.add( requestDataResolver );
		resolvers.add( signResolver );
		
		return resolvers;
	}
	
	/**
	 * 获取返回参数处理的HandlerMethodReturnValueHandler实现类
	 * @param adapter
	 * @return List<HandlerMethodReturnValueHandler>
	 */
	protected List<HandlerMethodReturnValueHandler> getCustomerReturnValueHandler( RequestMappingHandlerAdapter adapter ) {
		
		//处理签名、验签
		HandlerMethodReturnValueHandler signHandler = new SignResponseProcessor( adapter.getMessageConverters() );
		applicationContext.getAutowireCapableBeanFactory().initializeBean( signHandler, SignResponseProcessor.class.getName() );
		
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();
		handlers.add( signHandler );
		
		return handlers;
		
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
