package net.dwade.plugins.resteasy;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import net.dwade.annotation.RequestDataBody;

/**
 * 支持resteasy对{@link RequestDataBody}注解的参数解析
 * @see RequestDataBody
 * @author huangxf
 * @date 2017年4月21日
 */
public class RequestDataInjector implements ValueInjector {
	
	private Parameter paramerter;
	
	private ResteasyProviderFactory providerFactory;
	
	private Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	/**
	 * 保存请求json数据的key，value为JSONObject
	 */
	public static final String JSON_KEY = RequestDataInjector.class.getName();
	
	public RequestDataInjector(Parameter parameter,	ResteasyProviderFactory providerFactory) {
		this.paramerter = parameter;
		this.providerFactory = providerFactory;
	}

	@Override
	public Object inject( HttpRequest request, HttpResponse response ) {
		
		Throwable ex = null;
		try {
			
			checkJsonRequest( request );
			
			JSONObject jsonObject = (JSONObject)request.getAttribute( JSON_KEY );
			
			//说明方法中的参数未被解析，因为可能需要对Sign进行解析
			if ( jsonObject == null  ) {
				
				//获取请求数据中的json
				String json = JSON.parseObject( request.getInputStream(), StandardCharsets.UTF_8, String.class );
				logger.info( "解析Json数据:{}", json );
				
				//转换成json对象
				jsonObject = JSONObject.parseObject( json );
				
				//保存至上下文中，便于其他参数的解析
				request.setAttribute( JSON_KEY, jsonObject );
			}
			
			//将data节点下面的数据返回
			return jsonObject.getJSONObject( "data" ).toJavaObject( paramerter.getType() );
			
		} catch ( Exception e ) {
			ex = e;
			logger.warn( "请求参数有误.", e );
		}
		throw new IllegalArgumentException( "Request param error.", ex );
	}
	
	/**
	 * 检验是否传递的是json数据
	 */
	protected void checkJsonRequest( HttpRequest request ) {

		String contentType = request.getHttpHeaders().getHeaderString( "Content-Type" );
		
		//匹配application/json，text/json
		if ( StringUtils.containsIgnoreCase( contentType, "json" ) ) {
			return;
		}
		throw new IllegalArgumentException( "Not json request." );
	}
	
	@Override
	public Object inject() {
		throw new RuntimeException("Not allowed to inject.");
	}


}
