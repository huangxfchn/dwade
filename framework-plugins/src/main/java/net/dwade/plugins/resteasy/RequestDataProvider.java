package net.dwade.plugins.resteasy;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.jaxrs.FastJsonProvider;

import net.dwade.annotation.RequestDataBody;

/**
 * 扩展javax.ws.rs的SPI，需要在META-INF中的javax.ws.rs.ext.Providers指定该类
 * @see FastJsonProvider
 * @author huangxf
 * @date 2017年4月21日
 */
public class RequestDataProvider implements MessageBodyReader<Object> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		if ( annotations == null ) {
			return false;
		}
		for ( Annotation annotation : annotations ) {
			if ( RequestDataBody.class == annotation.annotationType() ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		String json = JSON.parseObject(entityStream, Charset.forName("UTF-8"), String.class );
		JSONObject jsonObject = JSONObject.parseObject( json );
		Object result = jsonObject.getJSONObject( "data" ).toJavaObject( genericType.getClass() );
		return result;
	}

}
