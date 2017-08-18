package net.dwade.utils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * <em>慎用属性copy! 即便是JDK1.7之后使用软引用提升性能，但是反射仍然会影响性能，尤其是频繁转换的场景</em>
 * @author huangxf
 * @date 2017年4月15日
 */
public abstract class BeanUtils {
	
	private static final String[] DATE_PATTERNS = 
			new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss.SSS", "yyyyMMddHHmmss"};
	
	static {
		DateConverter convert = new DateConverter();
		convert.setPatterns( DATE_PATTERNS );
		ConvertUtils.register( convert, Date.class );
	}
	
	/**
	 * 使用apache的BeanUtils进行属性copy，支持基本的类型转换，<em>慎用属性copy! 因为反射会影响性能，尤其是频繁转换的场景</em>
	 * @see org.apache.commons.beanutils.BeanUtils
	 * @param target
	 * @param source
	 * @return void
	 */
	public static void copyProperties( Object target, Object source ) {
		try {
			org.apache.commons.beanutils.BeanUtils.copyProperties( target, source );
		} catch ( Exception e) {
			throw new RuntimeException( "Bean convert exception.", e );
		}
	}
	
	/**
	 * 利用fastjson提供的反序列化功能，高效地将map转换成javabean，性能比apache的BeanUtils快2倍
	 * @see JSONObject#toJavaObject(Class)
	 * @see org.apache.commons.beanutils.BeanUtils#populate(Object, Map)
	 * @return T
	 */
	public static <T> T convertMap( Map<String, Object> map, Class<T> target ) {
		JSON json = new JSONObject( map );
		return JSONObject.toJavaObject( json, target );
	}
	
	/**
	 * 利用fastjson提供的asm功能，高效地将map转换成javabean
	 * @return T
	 */
	public static <T> T convertStringMap( Map<String, String> map, Class<T> target ) {
		Map<String, Object> source = new HashMap<String, Object>( map );
		JSON json = new JSONObject( source );
		return JSONObject.toJavaObject( json, target );
	}
	
	/**
	 * 转换List
	 * @see BeanUtils#copyProperties(Object, Object)
	 * @return List<T>
	 */
	@SuppressWarnings("unchecked")
	public static <T, S> List<T> convertList( List<S> list, Class<T> target ) {
		if ( CollectionUtils.isEmpty( list ) ) {
			return Collections.EMPTY_LIST;
		}
		List<T> targets = new ArrayList<T>( list.size() );
		for ( S source : list ) {
			try {
				T bean = target.newInstance();
				copyProperties( bean, source );
				targets.add( bean );
			} catch ( Exception e) {
				throw new IllegalArgumentException( "不支持的类型" + target.getName(), e );
			}
		}
		return targets;
	}
	
	/**
	 * 将bean转换为Map<Object, Object>
	 * @see BeanMap
	 * @return Map<Object, Object>
	 */
	public static Map<Object, Object> convertToMap( Object bean ) {
		
		Map<Object, Object> beanMap = new BeanMap( bean );
		
		// BeanMap的keys中包括了class，需要remove掉，但是不能直接在BeanMap上remove，因为BeanMap限制了remove
		Map<Object, Object> result = new HashMap<Object, Object>( beanMap );
		
		result.remove( "class" );
		return result;
	}
	
	/**
	 * 将bean转换为Map<String, Object>
	 * @param bean
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> convertToStringMap( Object bean ) {
		Map<Object, Object> beanMap = convertToMap( bean );
		Map<String, Object> stringMap = new HashMap<String, Object>( beanMap.size() );
		for ( Entry<Object, Object> entry : beanMap.entrySet() ) {
			String key = null;
			if ( entry.getKey() instanceof String ) {
				key = (String)entry.getKey();
			} else {
				key = ConvertUtils.convert( key );
			}
			stringMap.put( key, entry.getValue() );
		}
		return stringMap;
	}
	
	/**
	 * 获取bean的属性名
	 * @return String[]
	 */
	public static <T> String[] getFieldNames( Class<T> clazz ) {
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors( clazz );
		List<String> fields = new ArrayList<String>( pds.length );
		for ( PropertyDescriptor pd : pds ) {
			//因为每个类都有getClass方法
			if ( "class".equals( pd.getName() ) ) {
				continue;
			}
			fields.add( pd.getName() );
		}
		return fields.toArray( new String[ 0 ] );
	}
	
	/**
	 * 返回this.getClass().getSimpleName() + " [" + json  + " ]"，
	 * @param bean
	 * @return String
	 * @see SensitiveJsonFilter
	 */
	public static String toString( Object bean ) {
		if ( bean == null ) {
			return StringUtils.EMPTY;
		}
		StringBuilder builder = new StringBuilder( 64 );
		builder.append( bean.getClass().getSimpleName() ).append( '[' );
		builder.append( JsonUtils.toJson( bean ) );
		builder.append( ']' );
		return builder.toString();
	}

}
