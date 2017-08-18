package net.dwade.core;

import net.dwade.core.exception.ServiceException;

/**
 * 签名、验签异常
 * @author huangxf
 * @date 2017年8月18日
 */
public class SignException extends ServiceException {

	private static final long serialVersionUID = -4226520223583280112L;

	public SignException( String errorCode ) {
		super( errorCode, MsgCode.getMsg( errorCode ) );
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 */
	public SignException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}


	/**
	 * @param message
	 * @param throwable
	 */
	public SignException(String message, Throwable throwable) {
		super( MsgCode.SIGN_INVALID, message, throwable);
	}

	/**
	 * @param errorCode
	 * @param errorMessage
	 * @param throwable
	 */
	public SignException(String errorCode, String errorMessage,
			Throwable throwable) {
		super(errorCode, errorMessage, throwable);
	}

}
