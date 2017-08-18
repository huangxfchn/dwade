package net.dwade.core.sign;

import net.dwade.core.Partner;
import net.dwade.core.PaymentResponse;
import net.dwade.core.SignException;

/**
 * 渠道签名、验签
 * @author huangxf
 * @date 2017年4月10日
 */
public interface PartnerSignService {
	
	/**
	 * 验签
	 * @param partner	渠道
	 * @param data		原始数据
	 * @param signed	被签名后的字符串
	 * @throws SignException
	 * @return boolean
	 */
	public boolean checkSign( Partner partner, SignType signType, String data, String signed ) throws SignException;
	
	/**
	 * 签名
	 * @return String
	 */
	public Sign doSign( Partner partner, PaymentResponse<?> response, SignType signType ) throws SignException;
 
}
