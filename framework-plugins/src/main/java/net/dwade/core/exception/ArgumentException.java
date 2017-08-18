package net.dwade.core.exception;

import net.dwade.core.MsgCode;

/**
 * 参数异常
 * @author huangxf
 * @date 2017年5月3日
 */
public class ArgumentException extends BaseException {

	public static final String CODE = MsgCode.ILLEGAL_ARGS_EXCEPTION;

	private static final long serialVersionUID = 1L;

	/**
	 * 默认构造
	 */
	public ArgumentException() {
		super( CODE ); 
	}
	
	public ArgumentException( String message ) {
		super( CODE, message ); 
	}

	/**
	 * @param throwable  异常对象
	 */
	public ArgumentException( Throwable throwable ) {
		this( MsgCode.getMsg( CODE ), throwable );
	}

	/**
	 * @param message    无错误编码,自定义提示信息
	 * @param throwable 异常对象
	 */
	public ArgumentException( String message, Throwable throwable ) {  
		super( CODE, MsgCode.getMsg( CODE ), throwable );
	}

}