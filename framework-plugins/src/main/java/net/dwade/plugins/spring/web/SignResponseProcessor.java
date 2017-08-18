package net.dwade.plugins.spring.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;

import com.alibaba.fastjson.JSONObject;

import net.dwade.annotation.SignResponseBody;
import net.dwade.core.MsgCode;
import net.dwade.core.Partner;
import net.dwade.core.PaymentResponse;
import net.dwade.core.PaymentSignResponse;
import net.dwade.core.SignException;
import net.dwade.core.sign.PartnerKeyRepository;
import net.dwade.core.sign.PartnerSign;
import net.dwade.core.sign.PartnerSignService;
import net.dwade.core.sign.Sign;
import net.dwade.core.sign.SignType;
import net.dwade.core.sign.internal.ConfigKeyRepository;
import net.dwade.core.sign.internal.SimplePartnerSignServiceSupport;
import net.dwade.core.support.PartnerObject;
import net.dwade.core.support.SignResponseAdapter;
import net.dwade.core.support.SystemDefaultPartner;
import net.dwade.utils.JsonUtils;

/**
 * 处理Controller方法上面的{@link SignResponseBody}、用于对响应的数据进行签名处理，并作为json数据返回
 * @see SignResponseBody
 * @author huangxf
 * @date 2017年4月11日
 */
public class SignResponseProcessor extends AbstractMessageConverterMethodProcessor 
	implements InitializingBean, ApplicationContextAware {
	
	private final Logger logger = LoggerFactory.getLogger( SignResponseProcessor.class );
	
	private ApplicationContext applicationContext;
	
	/**
	 * 验签、签名Service
	 */
	@Autowired( required = false )
	private PartnerSignService signService;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public void setSignService(PartnerSignService signService) {
		this.signService = signService;
	}

	/**
	 * @param converters
	 * @param manager
	 * @param requestResponseBodyAdvice
	 */
	public SignResponseProcessor(
			List<HttpMessageConverter<?>> converters,
			ContentNegotiationManager manager,
			List<Object> requestResponseBodyAdvice) {
		super(converters, manager, requestResponseBodyAdvice);
	}

	/**
	 * @param converters
	 * @param manager
	 */
	public SignResponseProcessor(
			List<HttpMessageConverter<?>> converters,
			ContentNegotiationManager manager) {
		super(converters, manager);
	}

	/**
	 * @param converters
	 * @param requestResponseBodyAdvice
	 */
	public SignResponseProcessor(
			List<HttpMessageConverter<?>> converters,
			List<Object> requestResponseBodyAdvice) {
		super(converters, null, requestResponseBodyAdvice);
	}

	/**
	 * @param converters
	 */
	public SignResponseProcessor(
			List<HttpMessageConverter<?>> converters) {
		super(converters);
	}

	/**
	 * 只处理返回值
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return false;
	}

	/**
	 * 判断方法返回值，是否支持处理
	 */
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return returnType.hasMethodAnnotation( SignResponseBody.class ) && 
				returnType.getMethod().getReturnType().isAssignableFrom( PaymentResponse.class );
	}

	/**
	 * 不需要解析参数
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return null;
	}
	
	/**
	 * 处理方法返回值，如果是正常数据则转换为{@link PaymentSignResponse}
	 * @return Object
	 */
	@SuppressWarnings("rawtypes")
	protected Object handleSignResponse( HttpServletRequest request, MethodParameter parameter, Object returnValue ) {
		
		Assert.notNull( returnValue, "@SignResponseBody method return value must be not null!" );
		
		//在supportsReturnType方法中已经校验为PaymentResponse实现类
		PaymentResponse resp = (PaymentResponse)returnValue;
		
		if ( !resp.isSuccess() ) {
			return resp;
		}
		
		Object data = resp.getData();
		
		//data节点是Null，无法进行加密，抛出异常
		if ( data == null ) {
			RuntimeException e = new SignException( MsgCode.SIGN_NO_DATA );
			logger.error( "handle signResponse error.", e );
			throw e;
		}
		
		//从Model容器、或者返回值中获取partner
		Partner partner = this.readPartner( request, data );
		
		//使用系统默认的商户进行数据签名
		if ( partner == null || StringUtils.isBlank( partner.getPartnerId() ) ) {
			logger.warn( "无法获取partnerId, 将使用SystemDefaultPartner进行数据签名处理." );
			partner = new SystemDefaultPartner();
		}
		
		SignResponseBody annotation = parameter.getMethodAnnotation( SignResponseBody.class );
		return this.doSign( annotation, resp, partner );
		
	}
	
	/**
	 * 获取partnerId，获取不到返回null
	 */
	private Partner readPartner( HttpServletRequest request, Object returnVal ) {
		
		// 首先尝试从ModelAndViewContainer中获取partnerId, 见RequestDataConverterProcessor#resolveArgument()
		PartnerSign sign = (PartnerSign)request.getAttribute( SignConstants.REQUEST_PARTNER_KEY );
		if ( sign != null ) {
			String partnerId = sign.getPartnerId();
			return new PartnerObject( partnerId, null );
		}
		JSONObject json = (JSONObject)request.getAttribute( SignConstants.REQUEST_JSON_KEY );
		if ( json != null ) {
			String partnerId = JsonUtils.getNestedValue( json, "data.partnerId" );
			return new PartnerObject( partnerId, null );
		}
		
		//如果data节点实现了Partner，则直接返回
		if ( Partner.class.isAssignableFrom( returnVal.getClass() ) ) {
			return (Partner)returnVal;
		}

		//如果未实现了Partner接口，尝试反射获取partnerId
		Partner partner = null;
		try {
			String partnerId = BeanUtils.getProperty( returnVal, "partnerId" );
			if ( StringUtils.isNotBlank( partnerId ) ) {
				partner = new PartnerObject( partnerId, null );
			}
		} catch (Exception e) {
			// ignore exception
		}
		return partner;
	}
	
	/**
	 * 签名处理
	 * @return String
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected PaymentSignResponse doSign( SignResponseBody annotation, PaymentResponse response, Partner partner ) {
		SignType signType = annotation.signType();
		Sign signed = signService.doSign( partner, response, signType );
		return new SignResponseAdapter<PaymentSignResponse>( response, signed );
	}
	
	protected boolean checkRequired(MethodParameter parameter) {
		return true;
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
			throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

		mavContainer.setRequestHandled(true);
		ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
		ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
		
		HttpServletRequest servletRequest = webRequest.getNativeRequest( HttpServletRequest.class );
		returnValue = handleSignResponse( servletRequest, returnType, returnValue );

		// Try even with null return value. ResponseBodyAdvice could get involved.
		writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		PartnerKeyRepository repository = null;
		if ( applicationContext != null ) {
			try {
				repository = this.applicationContext.getBean( PartnerKeyRepository.class );
				logger.info( "PartnerKeyRepository was found, use:{}", repository );
			} catch (Exception e) {
				logger.warn( "No PartnerKeyRepository was found, use default:ConfigKeyRepository" );
			}
		}
		if ( repository == null ) {
			repository = new ConfigKeyRepository();
		}
		if ( signService == null ) {
			this.signService = new SimplePartnerSignServiceSupport( repository );
		}
	}

}
