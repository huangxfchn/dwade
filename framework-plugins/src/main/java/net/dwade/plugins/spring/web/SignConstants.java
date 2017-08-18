package net.dwade.plugins.spring.web;

import net.dwade.core.sign.PartnerSign;

public abstract class SignConstants {
	
	/**
	 * 将解析之后的JSONObject数据保存在HttpServletRequest中的的key
	 */
	public static final String REQUEST_JSON_KEY = RequestDataConverterProcessor.class.getName();
	
	/**
	 * 将解析之后的PartnerSign数据保存在HttpServletRequest中的的key
	 */
	public static final String REQUEST_PARTNER_KEY = PartnerSign.class.getName();

}
