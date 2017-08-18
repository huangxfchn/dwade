package net.dwade.core.sign;

import org.apache.commons.lang3.StringUtils;

import net.dwade.core.MsgCode;
import net.dwade.core.Partner;
import net.dwade.core.PaymentResponse;
import net.dwade.core.SignException;

/**
 * 签名、验签抽象类
 * @author huangxf
 * @date 2017年8月18日
 */
public abstract class AbstractPartnerSignService implements PartnerSignService {
	
	/**
	 * 判断是否支持验签
	 * @return boolean
	 */
	public abstract boolean supportsSignCheck( Partner partner, SignType signType );
	
	/**
	 * 判断是否支持签名
	 * @return boolean
	 */
	public abstract boolean supportsDoSign( Partner partner, SignType signType );
	
	@Override
	public boolean checkSign(Partner partner, SignType signType, String data,
			String signed) throws SignException {
		boolean supported = supportsSignCheck( partner, signType );
		if ( !supported ) {
			throw new SignException( MsgCode.SIGN_UNSUPPORTED, "不支持的验签：" + signType );
		}
		if ( partner == null || StringUtils.isBlank( partner.getPartnerId() ) ) {
			throw new SignException( MsgCode.SIGN_NO_PARTNER );
		}
		return doCheckSign( partner, signType, data, signed );
	}
	
	protected abstract boolean doCheckSign(Partner partner, SignType signType, String data,
			String signed);
	
	protected abstract Sign doInternalSign(Partner partner, PaymentResponse<?> response, SignType signType);
	
	@Override
	public Sign doSign(Partner partner, PaymentResponse<?> response,
			SignType signType) throws SignException {
		boolean supported = supportsDoSign(partner, signType);
		if ( !supported ) {
			throw new SignException( MsgCode.SIGN_UNSUPPORTED, "不支持的签名：" + signType );
		}
		if ( partner == null || StringUtils.isBlank( partner.getPartnerId() ) ) {
			throw new SignException( MsgCode.SIGN_NO_PARTNER );
		}
		return doInternalSign( partner, response, signType );
	}
	

}
