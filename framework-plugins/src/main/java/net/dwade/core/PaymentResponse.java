package net.dwade.core;

import java.io.Serializable;

/**
 * 支付统一响应接口
 * @author huangxf
 * @date 2017年4月10日
 */
public interface PaymentResponse<T> extends Serializable {
	
	/**
	 * @return String
	 */
	public String getRetMsg();
	
	/**
	 * 响应的编码，000000代表success
	 * @return String
	 */
	public String getRetCode();
	
	/**
	 * @return T
	 */
	public T getData();
	
	/**
	 * 是否success
	 * @return boolean
	 */
	public boolean isSuccess();
	
}
