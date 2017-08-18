package net.dwade.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpUtils {
	
	/** Ajax请求头 */
	private static final String XHR = "XMLHttpRequest";
	
	/** Http请求头 */
	private static final String REQUEST_HEAD = "X-Requested-With";
	
	private static final String ACCEPT_HEAD = "Accept";
	
	public static final String UTF_8 = "UTF-8";
	
	public static final char PARAM_SEPARATOR = '&';
	
	public static final char NAME_VALUE_SEPARATOR = '=';
	
	/**
	 * 从Content-Type中获取charset的pattern
	 */
	private static final Pattern charsetPattern = Pattern.compile( ".*charset=(.*)$" );
	
	private static final Logger logger = LoggerFactory.getLogger( HttpUtils.class );
	
	/**
	 * 是否是ajax请求
	 * @param request
	 * @return boolean
	 */
	public static boolean isAjaxRequest( HttpServletRequest request ) {
		String head = request.getHeader( REQUEST_HEAD );
		return StringUtils.equals(XHR, head);
	}
	
	/**
	 * 判断HttpServletRequest是否请求json数据
	 * @return boolean
	 */
	public static boolean isJsonRequest( HttpServletRequest request ) {
		String accepted = request.getHeader( ACCEPT_HEAD );
		if ( StringUtils.containsAny( accepted, "application/json", "text/json" ) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * 在输出流中将对象以json串的形式输出，并且关闭输出流
	 */
	public static void writeJson( HttpServletRequest request, HttpServletResponse response, Object data ) {
		writeJson( request, response, data, true );
	}
	
	/**
	 * 在输出流中将对象以json串的形式输出，由autoClose参数指定是否关闭流
	 */
	public static void writeJson( HttpServletRequest request, HttpServletResponse response, Object data, boolean autoClose ) {
		String json = JsonUtils.toJson( data );
		writeJson( request, response, json, autoClose );
	}
	
	/**
	 * 在输出流中将对象以json串的形式输出，并且关闭输出流
	 */
	public static void writeJson( HttpServletRequest request, HttpServletResponse response, String json ) {
		writeJson( request, response, json, true );
	}
	
	/**
	 * 在输出流中将对象以json串的形式输出，并且关闭输出流
	 */
	public static void writeJson( HttpServletRequest request, HttpServletResponse response, String json, boolean autoClose ) {
		//兼容不同的客户端请求
		if ( isIEBrower( request ) ) {
			response.setHeader("Content-type", "text/json");
		} else {
			response.setHeader("Content-type", "application/json");
		}
		
		PrintWriter pw = null;
		try {
			pw = response.getWriter();
			pw.write( json );
		} catch (IOException e) {
			throw new RuntimeException( "输出Json异常", e );
		} finally {
			if ( autoClose ) {
				IOUtils.closeQuietly(pw);
			}
		}
	}
	
	public static boolean isIEBrower( HttpServletRequest request ) {
		String userAgentHeader = request.getHeader( "User-Agent" );
		if ( StringUtils.isEmpty( userAgentHeader ) ) {
			return false;
		}
		String userAgent = userAgentHeader.toUpperCase();
		boolean lowerIE = StringUtils.indexOf( userAgent, "MSIE" ) >= 0;
		boolean isIE11 = StringUtils.indexOf( userAgent, "RV:11.0" ) >= 0;
		return lowerIE || isIE11;
	}
	
	public static boolean isHttps( URI uri ) {
		if ( uri == null ) {
			return false;
		}
		String url = uri.toString();
		return isHttps( url );
	}
	
	public static boolean isHttps( String url ) {
		if ( url == null ) {
			return false;
		}
		if ( url.trim().startsWith( "https" ) ) {
			 return true;
		}
		return false;
	}
	
	/**
	 * <p>去除{@link HttpServletRequest#getQueryString()}中的请求参数</p>
	 * eg：removeQueryString( "key1=value1&key2=value2", "key1" ) --> "key2=value2"
	 */
	public static String removeQueryString( String queryString, String key  ) {
		if ( StringUtils.isEmpty( queryString ) ) {
			return queryString;
		}
		String regex = "key" + "=([^&]*)(&|$)";
		return queryString.replaceAll( regex, StringUtils.EMPTY );
	}
	
	/**
	 * 删除多个key值
	 * @see #removeQueryString(String, String)
	 */
	public static String removeQueryStrings( String queryString, String... keys  ) {
		if ( ArrayUtils.isEmpty( keys ) ) {
			return queryString;
		}
		String result = queryString;
		for ( String key : keys ) {
			result = removeQueryString( result, key );
		}
		return result;
	}
	
	/**
	 * 递归获取数据
	 * <p>
	 * 	先从{@link HttpServletRequest}中获取key对应的value，
	 *  无对应的值再从{@link Cookie}中取
	 * </p>
	 * @return String
	 */
	public static String getRecursiveValue( HttpServletRequest request, String key ) {
		
		//先从request中取value，取不到再从cookies中取
		String value = request.getParameter( key );
		if ( value != null ) {
			return value;
		}
		
		//从cookies中取value
		Cookie[] cookies = request.getCookies();
		if ( cookies == null ) {
			return null;
		}
		for ( Cookie cookie : cookies ) {
			if ( StringUtils.equals( cookie.getName(), key ) ) {
				return cookie.getValue();
			}
		}
		
		return null;
		
	}
	
	/**
	 * 在url末尾拼接请求参数，编码格式UTF-8
	 * @see #contactQueryString(String, Map, String)
	 */
	public static String contactQueryString( String url, String key, String value ) {
		return contactQueryString( url, key, value, UTF_8 );
	}
	
	/**
	 * 在url末尾拼接请求参数
	 * @see #contactQueryString(String, Map, String)
	 */
	public static String contactQueryString( String url, String key, String value, String charset ) {
		Map<String, String> paramters = new HashMap<String, String>( 8 );
		paramters.put( key, value );
		return contactQueryString( url, paramters, charset );
	}
	
	/**
	 * 在url末尾拼接请求参数，编码格式UTF-8
	 * @see #contactQueryString(String, Map, String)
	 */
	public static String contactQueryString( String url, Map<String, String> paramters ) {
		return contactQueryString( url, paramters, UTF_8 );
	}
	
	/**
	 * 在url末尾拼接请求参数
	 */
	public static String contactQueryString( String url, Map<String, String> paramters, String charset ) {
		
		if ( StringUtils.isEmpty( url ) || MapUtils.isEmpty( paramters ) ) {
			return url;
		}
		
		//拼接参数
		String queryString = buildQueryString( paramters, charset, true );
		StringBuilder result = new StringBuilder( url );
		
		//如果没有带?
		if ( !StringUtils.contains( url, '?' ) ) {
			result.append( '?' ).append( queryString );
			return result.toString();
		}
		
		//如果url中带&
		if ( StringUtils.contains( url, PARAM_SEPARATOR ) ) {
			if ( !url.endsWith( "&" ) ) {
				result.append( PARAM_SEPARATOR );
			}
			result.append( queryString );
		} else {
			result.append( PARAM_SEPARATOR ).append( queryString );
		}
		
		return result.toString();
		
	}
	
	/**
	 * 将map转换为QueryString，并使用UTF-8对QueryString进行编码
	 * @return String
	 */
	public static String buildQueryString( Map<String, String> request ) {
		return buildQueryString( request, UTF_8, true );
	}
	
	/**
	 * 将map转换为QueryString，返回：key1=value1&key2=value2
	 * @param request
	 * @param ifUrlEncoded	如果为true则对返回的QueryString进行编码
	 * @return String
	 */
	public static String buildQueryString( Map<String, String> request, boolean ifUrlEncoded ) {
		return buildQueryString( request, UTF_8, ifUrlEncoded );
	}
	
	/**
	 * 将map转换为QueryString，返回：key1=value1&key2=value2
	 * @param request
	 * @param charset	编码格式，eg:UTF-8
	 * @param ifUrlEncoded	如果为true则对返回的QueryString进行编码，为false则不编码处理
	 * @return String
	 */
	public static String buildQueryString( Map<String, String> request, String charset, boolean ifUrlEncoded ) {
		if ( MapUtils.isEmpty( request ) ) {
			return StringUtils.EMPTY;
		}
		StringBuilder queryString = new StringBuilder();
		for ( Entry<String, String> entry : request.entrySet() ) {
			String key = handleQueryString( entry.getKey(), charset, ifUrlEncoded );
			String value = handleQueryString( entry.getValue(), charset, ifUrlEncoded );
			queryString.append( key ).append( NAME_VALUE_SEPARATOR );
			queryString.append( value ).append( PARAM_SEPARATOR );
		}
		queryString.deleteCharAt( queryString.length() - 1 );
		return queryString.toString();
	}
	
	/**
	 * @see #buildObjectQueryString(Map, boolean)
	 */
	public static String buildObjectQueryString( Map<String, Object> request ) {
		return buildObjectQueryString( request, UTF_8, true );
	}
	
	/**
	 * 将map转换为QueryString，并且把map中的value转换为String，返回：key1=value1&key2=value2
	 * @param request
	 * @param ifUrlEncoded	如果为true则对返回的QueryString进行编码，为false则不编码处理
	 * @return String
	 */
	public static String buildObjectQueryString( Map<String, Object> request, boolean ifUrlEncoded ) {
		return buildObjectQueryString( request, UTF_8, ifUrlEncoded );
	}
	
	/**
	 * 将map转换为QueryString，返回：key1=value1&key2=value2
	 * @param request
	 * @param charset
	 * @param ifUrlEncoded	如果为true则对返回的QueryString进行编码，为false则不编码处理
	 * @return String
	 */
	public static String buildObjectQueryString( Map<String, Object> request, String charset, boolean ifUrlEncoded ) {
		if ( MapUtils.isEmpty( request ) ) {
			return StringUtils.EMPTY;
		}
		StringBuilder queryString = new StringBuilder();
		for ( Entry<String, Object> entry : request.entrySet() ) {
			String formatted = ConvertUtils.convert( entry.getValue() );
			String key = handleQueryString( entry.getKey(), charset, ifUrlEncoded );
			String value = handleQueryString( formatted, charset, ifUrlEncoded );
			queryString.append( key ).append( NAME_VALUE_SEPARATOR );
			queryString.append( value ).append( PARAM_SEPARATOR );
		}
		queryString.deleteCharAt( queryString.length() - 1 );
		return queryString.toString();
	}
	
	private static String handleQueryString( String field, String charset, boolean ifUrlEncoded ) {
		if ( ifUrlEncoded && field != null ) {
			return EncodingUtils.urlEncode( field, charset );
		}
		return field;
	}
	
	/**
	 * 将<code>HttpServletRequest</code>中的请求参数封装至Map中，values的可能是String，
	 * 也可能是String[]
	 * @return Map<String,Object>
	 */
	public static Map<String, Object> getParameters( HttpServletRequest request ) {
		
		Assert.notNull( request );
		
		//所有请求数据的name
		Enumeration<String> attrNames = request.getParameterNames();
		
		Map<String, Object> result = new HashMap<String, Object>();
		while ( attrNames.hasMoreElements() ) {
			
			String key = attrNames.nextElement();
			
			//兼容请求参数中的数组
			String[] values = request.getParameterValues( key );
			if ( values != null && values.length == 1 ) {
				result.put( key, values[0] );
			} else {
				result.put( key, values );
			}
		}
		return result;
	}
	
	/**
	 * 获取所有的请求头 
	 * @param request
	 * @return Map<String,String>
	 */
	public static Map<String, String> getHeaders( HttpServletRequest request ) {
		
		Assert.notNull( request );
		
		Map<String, String> result = new HashMap<String, String>();
		Enumeration<String> names = request.getHeaderNames();
		if ( names == null ) {
			return result;
		}
		while ( names.hasMoreElements() ) {
			String name = names.nextElement();
			result.put( name, request.getHeader( name ) );
		}
		return result;
	}
	
	/**
	 * 首先根据request的编码读取body数据，如果request未指定，则使用UTF-8读取，
	 * 如果读取异常，则返回空字符串，而不是抛出异常
	 * @param request
	 * @return String
	 */
	public static String getTextBody( HttpServletRequest request ) {
		try {
			Charset charset = getCharacterEncoding( request, StandardCharsets.UTF_8 );
			return IOUtils.toString( request.getInputStream(), charset );
		} catch (Exception e) {
			logger.error( e.getMessage(), e );
			return StringUtils.EMPTY;
		}
	}
	
	/**
	 * 使用默认的编码读取body数据，如果读取异常，则返回空字符串，而不是抛出异常
	 * @param request
	 * @return String
	 */
	public static String getTextBody( HttpServletRequest request, String charset ) {
		Assert.notNull( charset );
		try {
			return IOUtils.toString( request.getInputStream(), charset );
		} catch (Exception e) {
			logger.error( e.getMessage(), e );
			return StringUtils.EMPTY;
		}
	}
	
	/**
	 * 返回请求中的CharacterEncoding，如果获取不到，则返回Content-Type中设置的charset
	 */
	public static final Charset getCharacterEncoding( HttpServletRequest request ) {
		return getCharacterEncoding( request, null );
	}
	
	/**
	 * 返回请求中的CharacterEncoding，如果获取不到，则返回Content-Type中设置的charset，获取不到返回defaultCharset
	 */
	public static final Charset getCharacterEncoding( HttpServletRequest request, Charset defaultCharset ) {
		
		String encoding = request.getCharacterEncoding();
		
		// 如果characterEncoding获取不到，则从Content-Type中获取
		if ( StringUtils.isBlank( encoding ) && StringUtils.isNotBlank( request.getContentType() ) ) {
			Matcher matcher = charsetPattern.matcher( request.getContentType() );
			if ( matcher.find() ) {
				//group()是获取整个匹配的字符串
				encoding = matcher.group( 1 );
			}
		}
		
		// 如果还是空，则返回defaultCharset
		if ( StringUtils.isBlank( encoding ) ) {
			return defaultCharset;
		}
		
		try {
			return Charset.forName( encoding );
		} catch (Exception e) {
			// ignore exception
		}
		return defaultCharset;
		
	}

}
