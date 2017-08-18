package net.dwade.core.sign;

import net.dwade.core.Partner;

/**
 * 渠道签名密钥仓库
 * @author huangxf
 * @date 2017年4月10日
 */
public interface PartnerKeyRepository {

	/**
	 * 根据{@link Partner}获取密钥
	 * @return String
	 */
	public String getKey( Partner partner );
	
	/**
	 * 更新密钥
	 * @return String
	 */
	public void update( Partner partner, String key );
	
	/**
	 * 保存密钥
	 * @return String
	 */
	public void save( Partner partner, String key );
	
	/**
	 * 删除密钥
	 * @return String
	 */
	public void remove( Partner partner );
	
}
