package net.dwade.utils;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * @author huangxf
 * @date 2016年9月20日
 */
public abstract class JsonUtils {
	
	public static final int DEFAULT_PARSER_FEATURE;
	
    static {
        int features = 0;
        features |= Feature.AutoCloseSource.getMask();
        features |= Feature.InternFieldNames.getMask();
        features |= Feature.UseBigDecimal.getMask();
        features |= Feature.AllowUnQuotedFieldNames.getMask();
        features |= Feature.AllowSingleQuotes.getMask();
        features |= Feature.InitStringFieldAsEmpty.getMask();
        features |= Feature.OrderedField.getMask();
        DEFAULT_PARSER_FEATURE = features;
    }
	
	/**
	 * 将Object转换为Json串，序列化map中的null值、空字段、使用日期格式化
	 * @param data
	 * @return String
	 */
	public static String toJson( Object data ) {
		return JSON.toJSONString( data, SerializerFeature.WriteMapNullValue, 
				SerializerFeature.WriteNullListAsEmpty, 
				SerializerFeature.WriteNonStringKeyAsString,
				SerializerFeature.WriteDateUseDateFormat );
	}
	
	/**
	 * 使用Filter输出json，并且序列化map中的null值、空字段、使用日期格式化
	 * @param data
	 * @param filter
	 * @return String
	 * @see SensitiveJsonFilter
	 */
	public static String toJson( Object data, SerializeFilter filter ) {
		return JSON.toJSONString( data, filter, 
				SerializerFeature.WriteMapNullValue, 
				SerializerFeature.WriteNullListAsEmpty, 
				SerializerFeature.WriteNonStringKeyAsString, 
				SerializerFeature.WriteDateUseDateFormat );
	}
	
	/**
	 * 使用自定义特性序列化对象
	 * @return String
	 */
	public static String toJson( Object data, SerializerFeature... features  ) {
		return JSON.toJSONString( data, features );
	}
	
	/**
	 * 将json字符串转化成JSON对象，注意：只能处理json对象，不能处理json数组
	 * @param json
	 * @return
	 */
	public static JSONObject toJsonObject( String json ) {
		return (JSONObject)JSONObject.parse( json, DEFAULT_PARSER_FEATURE );
	}
	
	/**
	 * 将Json串转换指定的Bean
	 * @param json
	 * @param clazz
	 * @return T
	 */
	public static <T> T toBean( String json, Class<T> clazz ) {
		return JSON.parseObject( json, clazz );
	}
	
	public static <T> T toBean( String json, TypeReference<T> type ) {
		return JSON.parseObject( json, type );
	}
	
	/**
	 * 将Json串转换为{@link Map}
	 * @param json
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> toObjectMap( String json ) {
		return JSON.parseObject( json, new TypeReference<Map<String, Object>>(){} );
	}
	
	/**
	 * 将Json串转换为{@link Map}
	 * @param json
	 * @return Map<String,String>
	 */
	public static Map<String, String> toMap( String json ) {
		return JSON.parseObject( json, new TypeReference<Map<String, String>>(){} );
	}
	
	/**
	 * 获取级联属性的value，获取不到时返回<code>null</code>而不是抛出异常，
	 * 这里不使用json串作为入参是出于性能考虑，避免多次获取value的时候，重复将String转成JSONObject.
	 * eg:
	 * <pre>
	 * JSONObject obj = JSON.parseObject( jsonStr );
	 * String userId = JsonUtils.getNestedValue( obj, "data.user.userId" );
	 * </pre>
	 * @param jsonObj
	 * @param property
	 * @return String
	 */
	public static String getNestedValue( JSONObject jsonObj, String property ) {
		
		if ( jsonObj == null || property == null ) {
			return null;
		}
		
		String[] properties = StringUtils.split( property, "." );
		int counts = properties.length;
		if ( counts == 1 ) {
			return jsonObj.getString( properties[ 0 ] );
		}
		
		//递归获取级联属性的值
		JSONObject nested = null;
		try {
			for ( int i = 0; i < counts - 1; i++ ) {
				nested = jsonObj.getJSONObject( properties[ i ] );
				if ( nested == null ) {
					return null;
				}
			}
			return nested.getString( properties[ counts - 1 ] );
		} catch (Exception e) {
			return null;
		}
		
	}
	
}
