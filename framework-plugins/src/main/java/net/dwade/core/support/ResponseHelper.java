package net.dwade.core.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.dwade.core.MsgCode;
import net.dwade.core.PaymentResponse;
import net.dwade.core.exception.BaseException;

public class ResponseHelper {
	
	private static final Pattern PATTERN_ERROR_CODE = Pattern.compile("\\[errorCode=(\\d{1,6})");
	
	private static final Pattern PATTERN_ERROR_MSG = Pattern.compile("errorMsg=(.*)\\]");
	
	public static <T> PaymentResponse<T> errorResponse( BaseException e  ) {
		return new DefaultResponse<T>( e.getErrorCode(), e.getErrorMsg() );
	}
	
	public static <T> PaymentResponse<T> errorResponse( BaseException e, T data ) {
		String retCode = e.getErrorCode();
		String retMsg = e.getErrorMsg();
		return new DefaultResponse<T>( retCode, retMsg, data );
	}
	
	public static <T> PaymentResponse<T> errorResponse( Throwable e ) {
		if ( e instanceof BaseException ) {
			BaseException ex = (BaseException)e;
			String retCode = ex.getErrorCode();
			String retMsg = ex.getErrorMsg();
			return new DefaultResponse<T>( retCode, retMsg );
		}
		return convertResponse( e, null );
	}
	
	public static <T> PaymentResponse<T> errorResponse( Throwable e, T data ) {
		if ( e instanceof BaseException ) {
			BaseException ex = (BaseException)e;
			String retCode = ex.getErrorCode();
			String retMsg = ex.getErrorMsg();
			return new DefaultResponse<T>( retCode, retMsg, data );
		}
		return convertResponse( e, data );
	}
	
	private static <T> PaymentResponse<T> convertResponse( Throwable ex, T data ) {
		
		String message = ex.getMessage();
		Matcher codeMathcer = PATTERN_ERROR_CODE.matcher( message );
		Matcher msgMathcer = PATTERN_ERROR_MSG.matcher( message );
		
		String retCode = null;
		String retMsg = null;
		
		// 首先从message中截取，主要是从dubbo包装的异常中提取errorCode，见ExceptionFilter
		if ( codeMathcer.find() ) {
			retCode = codeMathcer.group( 1 );
		}
		if ( msgMathcer.find() ) {
			retMsg = msgMathcer.group( 1 );
		}
		
		// 如果截取不到，则从MsgCode中获取
		if ( StringUtils.isBlank( retCode ) ) {
			retCode = MsgCode.getCode( ex );
		}
		if ( StringUtils.isBlank( retMsg ) ) {
			retMsg = MsgCode.getMsg( retCode );
		}
		
		return new DefaultResponse<T>( retCode, retMsg, data );
	}

}
