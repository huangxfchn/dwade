package net.dwade.plugins.spring.web;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.dwade.annotation.RequestDataBody;
import net.dwade.core.sign.PartnerSign;
import net.dwade.core.sign.Sign;
import net.dwade.core.sign.SignType;
import net.dwade.core.sign.internal.PartnerSignObject;
import net.dwade.core.sign.internal.SignObject;
import net.dwade.utils.JsonUtils;

/**
 * 在Controller中添加{@link RequestDataBody}、{@link Sign}、{@link PartnerSign}的支持
 * @author huangxf
 * @date 2017年4月10日
 */
public class RequestDataConverterProcessor extends
	AbstractMessageConverterMethodProcessor {
	
	private final Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	/**
	 * @param converters
	 * @param manager
	 * @param requestResponseBodyAdvice
	 */
	public RequestDataConverterProcessor(
			List<HttpMessageConverter<?>> converters,
			ContentNegotiationManager manager,
			List<Object> requestResponseBodyAdvice) {
		super(converters, manager, requestResponseBodyAdvice);
	}

	/**
	 * @param converters
	 * @param manager
	 */
	public RequestDataConverterProcessor(
			List<HttpMessageConverter<?>> converters,
			ContentNegotiationManager manager) {
		super(converters, manager);
	}

	/**
	 * @param converters
	 * @param requestResponseBodyAdvice
	 */
	public RequestDataConverterProcessor(
			List<HttpMessageConverter<?>> converters,
			List<Object> requestResponseBodyAdvice) {
		super(converters, null, requestResponseBodyAdvice);
	}

	/**
	 * @param converters
	 */
	public RequestDataConverterProcessor(
			List<HttpMessageConverter<?>> converters) {
		super(converters);
	}

	/**
	 * 判断方法参数是否支持解析
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation( RequestDataBody.class) 
				|| Sign.class.isAssignableFrom( parameter.getParameterType() )
				|| PartnerSign.class.isAssignableFrom( parameter.getParameterType() );
	}

	/**
	 * 判断方法返回值，是否支持处理
	 */
	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return false;
	}

	/**
	 * Throws MethodArgumentNotValidException if validation fails.
	 * @throws HttpMessageNotReadableException if {@link RequestBody#required()}
	 * is {@code true} and there is no body content or if there is no suitable
	 * converter to read the content with.
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		HttpServletRequest request = webRequest.getNativeRequest( HttpServletRequest.class );
		JSONObject json = (JSONObject)request.getAttribute( SignConstants.REQUEST_JSON_KEY );
		
		//从mavContainer取出json，如果不为空说明已经从Request中读取了json，这里主要目的是将解析的json串保存到容器中，便于后续的Sign参数解析
		if ( json == null ) {
			String jsonStr = (String)readWithMessageConverters( webRequest, parameter, String.class );
			json = JSON.parseObject( jsonStr );
			request.setAttribute( SignConstants.REQUEST_JSON_KEY, json );
		}
		Object arg = parseParameterFromJson( parameter, json );
		
		// 如果是PartnerSign，则将参数保存到容器中，便于后续的出参加密等操作
		if ( arg instanceof PartnerSign ) {
			request.setAttribute( SignConstants.REQUEST_PARTNER_KEY, arg );
		}
		
		parameter = parameter.nestedIfOptional();
		String name = Conventions.getVariableNameForParameter(parameter);

		WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
		if (arg != null) {
			validateIfApplicable(binder, parameter);
			if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
				throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
			}
		}
		mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());

		return adaptArgumentIfNecessary(arg, parameter);
		
	}
	
	/**
	 * 从json中解析方法参数，仅支持{@link RequestDataBody}注解解析、{@link Sign}、{@link PartnerSign}
	 * @param parameter
	 * @param json
	 * @return Object
	 */
	protected Object parseParameterFromJson(MethodParameter parameter, JSONObject json) {
		
		if ( json == null ) {
			throw new IllegalArgumentException( "请求参数有误!" );
		}
		
		Object obj = null;
		if ( parameter.hasParameterAnnotation( RequestDataBody.class ) ) {
			try {
				obj = json.getJSONObject( "data" ).toJavaObject( parameter.getParameterType() );
			} catch (Exception e) {
				// ignore exception.
				logger.warn( "请求参数有误" + json, e );
			}
			
			//判断是否是必须参数
			RequestDataBody annotation = parameter.getParameterAnnotation( RequestDataBody.class );
			if ( annotation.required() && obj == null ) {
				throw new IllegalArgumentException( "请求参数有误!" );
			}
			
			return obj;
		}
	
		/**
		 * 该类仅支持RequestDataBody、Sign、PartnerSign，其他参数解析不会进入以下方法
		 */
		String sign = json.getString( "sign" );
		String type = json.getString( "signType" );
		if ( sign == null ) {
			throw new IllegalArgumentException( "请求参数有误!" );
		}
		
		//默认使用MD5
		SignType signType = SignType.forName( type );
		if ( signType == null ) {
			signType = SignType.MD5;
		}
		
		// 对PartnerSign参数的解析
		Class<?> cls = parameter.getParameterType();
		if ( PartnerSign.class.isAssignableFrom( cls ) ) {
			String partnerId = JsonUtils.getNestedValue( json, "data.partnerId" );
			obj = new PartnerSignObject( sign, signType, partnerId );
		}
		
		// 对Sign参数的解析
		if ( Sign.class.isAssignableFrom( cls ) ) {
			obj = new SignObject( sign, signType );
		}
		
		return obj;
		
	}
	
	@Override
	protected <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter,
			Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {

		HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
		ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(servletRequest);

		Object arg = readWithMessageConverters( inputMessage, parameter, paramType );
		if (arg == null) {
			if (checkRequired(parameter)) {
				throw new HttpMessageNotReadableException("Required @RequestDataBody annatation is missing: " +
						parameter.getMethod().toGenericString());
			}
		}
		return arg;
	}

	protected boolean checkRequired(MethodParameter parameter) {
		return (parameter.getParameterAnnotation( RequestDataBody.class).required() && !parameter.isOptional());
	}

	@Override
	public void handleReturnValue(Object returnValue, MethodParameter returnType,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest)
			throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

		mavContainer.setRequestHandled(true);
		ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
		ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);

		// Try even with null return value. ResponseBodyAdvice could get involved.
		writeWithMessageConverters(returnValue, returnType, inputMessage, outputMessage);
	}
	
}
