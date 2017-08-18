package net.dwade.core;

/**
 * 常量
 * @author huangxf
 * @date 2017年8月18日
 */
public interface Constants {

	/**  Session相关常量  */
	public interface Session {
		
		/**  session中保存用户信息的key值  */
		String USER_KEY = SessionUser.class.getName();
		
	}
	
	/** 空字符串 */
	String EMPTY_STR = "";
	
	String YES = "Y";
	
	String NO = "N";

	String UTF_8 = "UTF-8";

}
