package net.dwade.plugins.rest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.MethodInjectorImpl;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import net.dwade.annotation.CheckSign;
import net.dwade.annotation.SignResponseBody;
import net.dwade.core.MsgCode;
import net.dwade.core.Partner;
import net.dwade.core.PaymentResponse;
import net.dwade.core.PaymentSignResponse;
import net.dwade.core.SignException;
import net.dwade.core.sign.PartnerSignService;
import net.dwade.core.sign.Sign;
import net.dwade.core.sign.SignType;
import net.dwade.core.support.PartnerObject;
import net.dwade.core.support.PartnerRequest;
import net.dwade.core.support.SignResponseAdapter;
import net.dwade.utils.JsonUtils;
import net.dwade.utils.MD5Utils;

/**
 * 扩展MethodInjector实现，用于扩展验签、数据加密等功能
 * 
 * @author huangxf
 * @date 2017年4月21日
 */
public class PaymentMethodInjector extends MethodInjectorImpl {
    
	private static Logger logger = LoggerFactory.getLogger(PaymentMethodInjector.class);
	
	private PartnerSignService signService;
	
	public PartnerSignService getSignService() {
		return signService;
	}

	public void setSignService(PartnerSignService signService) {
		this.signService = signService;
	}

	public PaymentMethodInjector(ResourceLocator resourceMethod, ResteasyProviderFactory factory) {
		super(resourceMethod, factory);
	}

	@Override
	public Object invoke(HttpRequest request, HttpResponse httpResponse, Object resource)
			throws Failure, ApplicationException {

		String flag = getQueryParameter(request, "_flag");// _flag = 1
		String bodyData = getBodyData(request.getInputStream());
		logger.info("请求体参数：{}，是否为加密数据:{}", bodyData, flag);
		Object value = null;
		// 验签 判断有没有 CheckSign注解
		CheckSign check = this.interfaceBasedMethod.getAnnotation(CheckSign.class);
		if (check != null) {
			logger.info("开始验签，入参为:{}", bodyData);
			this.checkSign(bodyData);
		}
		
		// 在调用invoke之前回去调用请求参数的处理
		value = super.invoke(request, httpResponse, resource);
		
		//判断有没有SignResponseBody
		SignResponseBody response = this.interfaceBasedMethod.getAnnotation(SignResponseBody.class);
		if (response != null &&  PaymentResponse.class.isAssignableFrom(value.getClass())) {
			
			PaymentResponse<?> paymentResponse = (PaymentResponse<?>)value;
			Object data = paymentResponse.getData();
			//data节点是Null，不进行加密，因为是异常数据
			if (data == null) {
				return JsonUtils.toJson(paymentResponse);
			}
			
			//获取partner 
			Partner partner = this.readPartner(request, data);
			logger.info("开始签名，返回参数为:{}", value);
			PaymentSignResponse<?> signResp = this.doSign(response, paymentResponse, partner);
			
			//修改全局日期
			//JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
			return JsonUtils.toJson(signResp, SerializerFeature.WriteDateUseDateFormat);
		}
		return value;
	}

	/**
	 * 验签
	 */
	protected void checkSign(String bodyData) {

		HashMap<String, Object> requestContent = JSON.parseObject(bodyData, new TypeReference<HashMap<String, Object>>(){}, Feature.OrderedField);
		
		if (requestContent.get("data") == null || requestContent.get("signType") == null || requestContent.get("sign") == null) {
			throw new SignException(MsgCode.ILLEGAL_ARGS_EXCEPTION);
		}
		
		String data = String.valueOf(requestContent.get("data"));
		JSONObject jsonobject = JSONObject.parseObject(data);
		PartnerRequest partner = new PartnerRequest();
		partner.setPartnerId(jsonobject.get("partnerId").toString());
		SignType signType = SignType.valueOf(requestContent.get("signType").toString());
		String signed = (String) requestContent.get("sign");
		
		boolean supported = supportsSignCheck( partner, signType );
		if ( !supported ) {
			throw new SignException( MsgCode.SIGN_UNSUPPORTED, "不支持的验签：" + signType );
		}
		if ( partner == null || StringUtils.isBlank( partner.getPartnerId() ) ) {
			throw new SignException( MsgCode.SIGN_NO_PARTNER );
		}
		
		Boolean isSign = doCheckSign( partner, signType, data, signed );
		if (!isSign) {
			throw new SignException(MsgCode.SIGN_CHECK_EXCEPTION);
		}
	}

	protected boolean doCheckSign(Partner partner, SignType signType, String data, String signed) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("CheckSign:the request data resource is [{}]", data);
		}
		StringBuilder builder = new StringBuilder(100);
		builder.append(data).append("&key=").append(signService.getSignKey(partner.getPartnerId()));
		String sign = MD5Utils.md5(builder.toString());
		
		logger.info("CheckSign:the request sign is:[{}], but check result sign is:[{}]", signed, sign);
		
		return StringUtils.equalsIgnoreCase(sign, signed);
	}
	/**
	 * 判断是否支持验签
	 * 
	 * @return boolean
	 */
	protected boolean supportsSignCheck(Partner partner, SignType signType) {
		if (SignType.MD5 == signType) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取请求查询数据
	 * 
	 * @param request
	 * @param name
	 * @return
	 */
	private String getQueryParameter(HttpRequest request, String name) {

		ResteasyUriInfo uri = request.getUri();
		MultivaluedMap<String, String> multivaluedMap = uri.getQueryParameters(true);
		String value = multivaluedMap.getFirst(name);
		return value;
	}

	/**
	 * 获取请求体数据
	 * 
	 * @param inputStream
	 * @return
	 */
	private String getBodyData(InputStream inputStream) {

		BufferedReader in = null;
		StringBuffer buffer = new StringBuffer();

		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}

		} catch (Exception e) {
			logger.error("读取请求数据发生异常!", e);
		}

		return buffer.toString();
	}
	
	/**
	 * 获取partnerId，获取不到返回null
	 */
	private Partner readPartner(HttpRequest request, Object returnVal) {
		
		Object json = getBodyData(request.getInputStream());
		if (json != null) {
			JSONObject jsonObject = JsonUtils.toJsonObject(String.valueOf(json));
			String partnerId = JsonUtils.getNestedValue( jsonObject, "data.partnerId" );
			return new PartnerObject( partnerId, null );
		}
		
		//如果data节点实现了Partner，则直接返回
		if (Partner.class.isAssignableFrom(returnVal.getClass())) {
			return (Partner)returnVal;
		}

		//如果未实现了Partner接口，尝试反射获取partnerId
		Partner partner = null;
		try {
			String partnerId = BeanUtils.getProperty(returnVal, "partnerId");
			if (StringUtils.isNotBlank(partnerId)) {
				partner = new PartnerObject(partnerId, null);
			}
		} catch (Exception e) {
			// ignore exception
		}
		return partner;
	}
	
	/**
	 * 签名处理
	 * @param <T>
	 * @return String
	 */
	protected <T> PaymentSignResponse<T> doSign( SignResponseBody annotation, PaymentResponse<T> response, Partner partner ) {
		SignType signType = annotation.signType();
		Sign signed = signService.doSign( partner, response, signType );
		return new SignResponseAdapter<T>( response, signed );
	}
}
