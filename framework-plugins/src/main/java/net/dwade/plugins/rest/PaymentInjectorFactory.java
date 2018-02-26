package net.dwade.plugins.rest;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import net.dwade.annotation.RequestDataBody;
import net.dwade.core.sign.PartnerKeyRepository;
import net.dwade.core.sign.PartnerSignService;
import net.dwade.core.sign.internal.SimplePartnerSignServiceSupport;

/**
 * 扩展resteasy的{@link InjectorFactory}，用于实现自定义参数解析、参数验证，
 * 如果使用{@link SpringResteasyBootstrap}作为ServletContextListener启动，默认会使用该InjectorFactory，
 * 否则需要在web.xml中指定resteasy.injector.factory参数, eg:
 * <pre>
 * &lt;context-param&gt;
 *	&lt;param-name&gt;resteasy.injector.factory&lt;/param-name&gt;
 *	&lt;param-value&gt;com.sitech.miso.payment.common.support.rest.PaymentInjectorFactory&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * @see SpringResteasyBootstrap
 * @see ResteasyBootstrap
 * @see ListenerBootstrap#createDeployment()
 * @author huangxf
 * @date 2017年4月21日
 */
public class PaymentInjectorFactory extends InjectorFactoryImpl implements ApplicationListener<ContextRefreshedEvent> {
	
	@Resource
	private PartnerSignService partnerSignService;
	
	private Set<PaymentMethodInjector> providerProxySet = Collections.newSetFromMap(new ConcurrentHashMap<PaymentMethodInjector, Boolean>());
	
	@Override
	public ValueInjector createParameterExtractor(Parameter parameter,
			ResteasyProviderFactory providerFactory) {
		boolean isDataBody = hasAnnotation( parameter, RequestDataBody.class );
		if ( isDataBody ) {
			return new RequestDataInjector( parameter, providerFactory );
		}
		return super.createParameterExtractor(parameter, providerFactory);
	}
	
	/**
	 * 返回{@link PaymentMethodInjector}
	 */
	@Override
	public MethodInjector createMethodInjector(ResourceLocator method,
			ResteasyProviderFactory factory) {
		PaymentMethodInjector paymentMethodInjector = new PaymentMethodInjector( method, factory );
		providerProxySet.add(paymentMethodInjector);
		return paymentMethodInjector;
	}

	protected boolean hasAnnotation( Parameter parameter, Class<?> annotation ) {
		Annotation[] annotations = parameter.getAnnotations();
		if ( annotations == null ) {
			return false;
		}
		for ( Annotation anno : annotations ) {
			boolean is = annotation.isInstance( anno );
			if ( is ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext application = event.getApplicationContext();
		if (partnerSignService == null && application.containsBean("partnerSignService")) {
			this.partnerSignService = application.getBean(PartnerSignService.class);
		}
		if (partnerSignService == null && application.containsBean("partnerKeyRepository")) {
			this.partnerSignService = new SimplePartnerSignServiceSupport( application.getBean(PartnerKeyRepository.class) );
			application.getAutowireCapableBeanFactory().initializeBean( partnerSignService, PartnerSignService.class.getName() );
		}
		Iterator<PaymentMethodInjector> iterator = providerProxySet.iterator();
		while (iterator.hasNext()) {
			PaymentMethodInjector jnjector = iterator.next();
			jnjector.setSignService(partnerSignService);
		}
		application.getAutowireCapableBeanFactory().initializeBean(this, this.getClass().getName());
	}

}
