package net.dwade.core.exception;

/**  
 * dao层异常处理类
 * 2015-10-29 下午4:30:10 
 */ 
public class DaoException extends BaseException{

	private static final long serialVersionUID = 1L;

	/**
	 * 默认构造
	 */
	public DaoException()  
	{  
		super();  
	} 

	/**
	 * 
	 * @param errorCode 自定义错误编码
	 */
	public DaoException(String errorCode)  
	{  
		super(errorCode);  
	} 

	/**
	 * @param throwable  异常对象
	 */
	public DaoException(Throwable throwable)  
	{  
		super(throwable);  
	} 

	/**
	 * @param errorCode  自定义错误编码
	 * @param errorMessage  自定义错误信息
	 */
	public DaoException(String errorCode,String errorMessage)  
	{  
		super(errorCode,errorMessage);  
	}  

	/**
	 * @param errorCode  自定义错误编码
	 * @param errorMessage 自定义格式化错误信息
	 * @param throwable 异常对象
	 */
	public DaoException(String errorCode,String message,Throwable throwable)  
	{  
		super(errorCode,message,throwable);  
	}  

	/**
	 * @param message    无错误编码,自定义格式化错误信息
	 * @param throwable 异常对象
	 */
	public DaoException(String message,Throwable throwable)  
	{  
		super(message,throwable);  
	} 

}
