package net.dwade.core.sign;

/**
 * 签名接口
 * @author huangxf
 * @date 2017年4月10日
 */
public interface Sign {
	
	/**
	 * 返回签名串
	 * @return String
	 */
	public String getSign();

	/**
	 * 签名类型
	 */
	public SignType getSignType();
	
	/**
	 * 主体数据
	 * @return String
	 */
	public String getData();

}
