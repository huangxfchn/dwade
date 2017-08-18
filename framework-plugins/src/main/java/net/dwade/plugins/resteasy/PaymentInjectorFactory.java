package net.dwade.plugins.resteasy;

import java.lang.annotation.Annotation;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

import net.dwade.annotation.RequestDataBody;

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
public class PaymentInjectorFactory extends InjectorFactoryImpl {
	
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
		return new PaymentMethodInjector( method, factory );
	}


	@SuppressWarnings("rawtypes")
	protected boolean hasAnnotation( Parameter parameter, Class annotation ) {
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

}
