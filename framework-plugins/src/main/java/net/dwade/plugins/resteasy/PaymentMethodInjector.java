package net.dwade.plugins.resteasy;

import org.jboss.resteasy.core.MethodInjectorImpl;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dwade.annotation.CheckSign;

/**
 * 扩展MethodInjector实现，用于扩展验签、数据加密等功能
 * @author huangxf
 * @date 2017年4月21日
 */
public class PaymentMethodInjector extends MethodInjectorImpl {
	
	private static Logger logger = LoggerFactory.getLogger( PaymentMethodInjector.class );
	
	public PaymentMethodInjector(ResourceLocator resourceMethod,
			ResteasyProviderFactory factory) {
		super(resourceMethod, factory);
	}
	
	@Override
	public Object invoke(HttpRequest request, HttpResponse httpResponse,
			Object resource) throws Failure, ApplicationException {
		
		//TODO 验签
		CheckSign check = this.interfaceBasedMethod.getAnnotation( CheckSign.class );
		if ( check != null ) {
			// 根据注解指定的bean进行验签
			String beanName = check.beanName();
		}
		
		Object value = super.invoke(request, httpResponse, resource);
		
		//TODO 签名
		return value;
	}

}
