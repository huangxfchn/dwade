package net.dwade.plugins.spring.web;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import net.dwade.annotation.CheckSign;
import net.dwade.core.MsgCode;
import net.dwade.core.Partner;
import net.dwade.core.PaymentResponse;
import net.dwade.core.sign.PartnerKeyRepository;
import net.dwade.core.sign.PartnerSign;
import net.dwade.core.sign.PartnerSignService;
import net.dwade.core.sign.internal.ConfigKeyRepository;
import net.dwade.core.sign.internal.SimplePartnerSignServiceSupport;
import net.dwade.core.support.DefaultResponse;
import net.dwade.core.support.PartnerObject;
import net.dwade.core.support.ServiceResult;
import net.dwade.utils.HttpUtils;
import net.dwade.utils.JsonUtils;

/**
 * 用于对请求的数据进行签名处理，使用拦截器的方式实现，同时为了保证{@link HttpServletRequest}的InputStream可重复读取，
 * 使用了其包装类{@link BufferedHttpServletRequestWrapper}
 * @author huangxf
 * @date 2017年8月8日
 */
public class CheckSignInterceptorProcessor extends HandlerInterceptorAdapter implements 
	InitializingBean, ApplicationContextAware {
	
    private final Logger logger = LoggerFactory.getLogger( CheckSignInterceptorProcessor.class );
    
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

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if ( !(handler instanceof HandlerMethod) ) {
			return true;
		}
		
		// 判断是否包含@CheckSign注解
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		boolean ifCheck = handlerMethod.hasMethodAnnotation( CheckSign.class );
		if ( !ifCheck ) {
			return true;
		}
		
		// 进行验签处理
		return this.doCheckSign( request, response, handlerMethod );
		
	}
	
	/**
	 * 进行验签处理
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected boolean doCheckSign( HttpServletRequest request, HttpServletResponse response, HandlerMethod handler ) {
		
		JSONObject json = (JSONObject)request.getAttribute( SignConstants.REQUEST_JSON_KEY );
		if ( json == null ) {
			String body = HttpUtils.getTextBody( request );
//			json = JsonUtils.toJsonObject( body );
			HashMap requestBody = JSON.parseObject(body, LinkedHashMap.class, Feature.OrderedField);
			json =(JSONObject) JSONObject.toJSON(requestBody);
		}
		
		// 获取渠道传递的sign，以及获取渠道来源
		String signed = json.getString( "sign" );
		String source = JsonUtils.toJson( json.getJSONObject( "data" ) );
		Partner partner = this.readPartner( request, json );
		if ( StringUtils.isEmpty( signed ) || partner == null ) {
			throw new IllegalArgumentException( "验签失败，请求参数有误!" );
		}
		
		// 进行验签处理
		CheckSign annotation = handler.getMethodAnnotation( CheckSign.class );
		boolean checked = signService.checkSign( partner, annotation.signType(), source, signed );
		
		// 如果验签失败，则直接响应
		if ( !checked ) {
			ServiceResult result = ServiceResult.build( false, MsgCode.SIGN_CHECK_EXCEPTION );
			PaymentResponse data = new DefaultResponse( result );
			HttpUtils.writeJson( request, response, data );
			return false;
		}
		return true;
		
	}
	
	/**
	 * 获取partnerId，获取不到返回null
	 */
	protected Partner readPartner( HttpServletRequest request, JSONObject requestJson ) {
		
		// 首先尝试从HttpServletRequest中获取partnerId, 见RequestDataConverterProcessor#resolveArgument()
		PartnerSign sign = (PartnerSign)request.getAttribute( SignConstants.REQUEST_PARTNER_KEY );
		if ( sign != null ) {
			String partnerId = sign.getPartnerId();
			return new PartnerObject( partnerId, null );
		}
		
		// 然后尝试从请求参数中获取partnerId
		String partnerId = JsonUtils.getNestedValue( requestJson, "data.partnerId" );
		return new PartnerObject( partnerId, null );
		
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
