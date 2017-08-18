package net.dwade.core.exception;

import net.dwade.core.MsgCode;

/**  
 * 业务异常处理类
 * 2015-10-29 下午4:30:41 
 */ 
public class ServiceException extends BaseException{

	private static final long serialVersionUID = 1L;

	/**
	 * 默认构造
	 */
	public ServiceException()  
	{  
		super();  
	} 

	/**
	 * 
	 * @param errorCode 自定义错误编码
	 */
	public ServiceException(String errorCode)  
	{  
		super(errorCode, MsgCode.getMsg( errorCode ));  
	} 

	/**
	 * @param throwable  异常对象
	 */
	public ServiceException(Throwable throwable)  
	{  
		super(throwable);  
	} 

	/**
	 * @param errorCode  自定义错误编码
	 * @param errorMessage  自定义错误信息
	 */
	public ServiceException(String errorCode,String errorMessage)  
	{  
		super(errorCode,errorMessage);  
	}  

	/**
	 * @param errorCode  自定义错误编码
	 * @param errorMessage 自定义错误信息
	 * @param throwable 异常对象
	 */
	public ServiceException(String errorCode,String errorMessage,Throwable throwable)  
	{  
		super(errorCode,errorMessage,throwable);  
	}  

	/**
	 * @param message    无错误编码,自定义提示信息
	 * @param throwable 异常对象
	 */
	public ServiceException(String message,Throwable throwable)  
	{  
		super(message,throwable);  
	} 

}
