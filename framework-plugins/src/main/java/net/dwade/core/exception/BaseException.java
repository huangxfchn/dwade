package net.dwade.core.exception;

import net.dwade.core.MsgCode;

/**  
* 基础异常处理类
* errorCode见message.properties
* 静态解析类 MessageCode
* 2015-10-29 下午4:28:27 
*/ 
public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_CODE = MsgCode.ERROR;
	
	private static final String DEFAULT_MSG = MsgCode.getMsg( DEFAULT_CODE );
	
	/**
	 * 自定义错误编码
	 */
	protected String errorCode;
	
	/**
	 * 自定义错误信息
	 */
	protected String errorMsg;

	/**
	 * 默认构造
	 */
	public BaseException() {
		this( DEFAULT_CODE, DEFAULT_MSG );
	}
	
	
	/**
	 * @param cause 异常对象
	 */
	public BaseException(Throwable cause) {
		this( DEFAULT_CODE, DEFAULT_MSG, cause );
	}

	/**
	 * @param errCode  自定义错误编码
	 */
	public BaseException(String errCode) {
		super(errCode);
		String em = MsgCode.getMsg(errCode);
		if (em == null) {
			this.errorCode = errCode;
			this.errorMsg="异常错误信息未配置";
		} else {
			this.errorCode = errCode;
			this.errorMsg = em;
		}
	}

	/**
	 * @param errCode 自定义 错误编码
	 * @param msg     自定义错误信息
	 */
	public BaseException(String errCode, String msg) {
		this( errCode, msg, null );
	}

	/**
	 * 
	 * @param message   自定义 错误 信息
	 * @param throwable 异常对象
	 */
	public BaseException(String message, Throwable e) {
		this( null, message, e );
	}

	/**
	 * @param errCode 自定义 错误编码
	 * @param message 自定义 错误 信息
	 * @param cause 异常对象
	 */
	public BaseException(String errCode, String message, Throwable e) {
		super( message, e );
		this.errorCode = errCode;
		this.errorMsg = message;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	@Override
	public String toString() {
		return "BaseException [errorCode=" + errorCode + ", errorMsg="
				+ errorMsg + "]";
	}

}
