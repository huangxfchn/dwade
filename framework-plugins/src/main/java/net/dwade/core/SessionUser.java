package net.dwade.core;

/**
 * Session中的用户信息接口
 * @author huangxf
 * @date 2017年4月11日
 */
public interface SessionUser {
	
	public String getUserId();
	
	public String getUserName();
	
	public String getPhoneNum();
	
	public String getCustomerId();

}
