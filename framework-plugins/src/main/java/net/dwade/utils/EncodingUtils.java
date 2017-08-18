package net.dwade.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public abstract class EncodingUtils {
	
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
	 * 使用utf-8对字符串编码
	 * @return String
	 */
	public static String toUtf8String( String input ) {
		if ( input == null ) {
			return StringUtils.EMPTY;
		}
		try {
			return new String( input.getBytes(), DEFAULT_ENCODING );
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported input string", e);
		}
	}

	/**
	 * 将byte[]转换为十六进制
	 */
	public static String hexEncode(byte[] input) {
		return Hex.encodeHexString(input);
	}

	/**
	 * 将input字符串转换成十六制编码的byte[]
	 */
	public static byte[] hexDecode(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw new IllegalStateException("Hex Decoder exception", e);
		}
	}

	public static String base64Encode(byte[] input) {
		return Base64.encodeBase64String(input);
	}

	public static String base64UrlSafeEncode(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}

	public static byte[] base64Decode(String input) {
		return Base64.decodeBase64(input);
	}

	public static String urlEncode(String input) {
		if ( input == null ) {
			return StringUtils.EMPTY;
		}
		return urlEncode(input, DEFAULT_ENCODING);
	}

	public static String urlEncode(String input, String encoding) {
		if ( input == null ) {
			return StringUtils.EMPTY;
		}
		try {
			return URLEncoder.encode(input, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported encode url", e);
		}
	}

	public static String urlDecode(String input) {
		if ( input == null ) {
			return StringUtils.EMPTY;
		}
		return urlDecode(input, DEFAULT_ENCODING);
	}

	public static String urlDecode(String input, String encoding) {
		if ( input == null ) {
			return StringUtils.EMPTY;
		}
		try {
			return URLDecoder.decode(input, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("Unsupported Encoding Exception", e);
		}
	}

}
