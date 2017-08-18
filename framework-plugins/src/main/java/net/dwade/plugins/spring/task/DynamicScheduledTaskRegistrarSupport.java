package net.dwade.plugins.spring.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.scheduling.config.ContextLifecycleScheduledTaskRegistrar;

/**
 * 改变Spring的{@link ContextLifecycleScheduledTaskRegistrar}，实现对定时任务的动态操作
 * @author huangxf
 * @date 2017年5月1日
 */
public class DynamicScheduledTaskRegistrarSupport implements BeanFactoryPostProcessor {
	
	private static Logger logger = LoggerFactory.getLogger( DynamicScheduledTaskRegistrarSupport.class );

	/**
	 * 改变Spring默认注册Task的任务Bean
	 */
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		boolean ifSupported = false;
		for ( String bfName : beanFactory.getBeanDefinitionNames() ) {
			BeanDefinition bf = beanFactory.getBeanDefinition( bfName );
			
			//在容器中可能有多个ContextLifecycleScheduledTaskRegistrar
			if ( ContextLifecycleScheduledTaskRegistrar.class.getName().equals( bf.getBeanClassName() ) ) {
				//改变默认的BeanClassName，改成自定义的DynamicScheduledTaskRegistrar实现对Task的操作扩展
				bf.setBeanClassName( DynamicScheduledTaskRegistrar.class.getName() );
				ifSupported = true;
			}
		}
		
		if ( !ifSupported ) {
			logger.warn( "DynamicScheduledTask not supported in this application, pleas check your configuration!" );
		}
		
	}

}
