package net.dwade.core;

/**
 * 带签名的响应结果，可以自定义sign签名
 * @author huangxf
 * @date 2017年4月8日
 */
public interface PaymentSignResponse<T> extends PaymentResponse<T> {
	
	/**
	 * 获取签名
	 * @return String
	 */
	public String getSign();

}
