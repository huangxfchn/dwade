package net.dwade.plugins.spring.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 扩展BeanFactory、BeanDefinitionRegistry
 * @author huangxf
 * @date 2017年4月10日
 */
public class PaymentPostProcessor implements BeanDefinitionRegistryPostProcessor {
	
	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}
	
	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		// 注册别名，便于取用RequestMappingHandlerAdapter对象（默认是全限定名）
		registry.registerAlias( RequestMappingHandlerAdapter.class.getName(), 
				"requestMappingHandlerAdapter" );
	}

}
