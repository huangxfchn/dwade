package net.dwade.core.sign.internal;

import org.apache.commons.lang3.StringUtils;

import net.dwade.core.MsgCode;
import net.dwade.core.Partner;
import net.dwade.core.PaymentResponse;
import net.dwade.core.PaymentSignResponse;
import net.dwade.core.SignException;
import net.dwade.core.sign.AbstractPartnerSignService;
import net.dwade.core.sign.PartnerKeyRepository;
import net.dwade.core.sign.Sign;
import net.dwade.core.sign.SignType;
import net.dwade.utils.JsonUtils;
import net.dwade.utils.MD5Utils;

/**
 * 签名、验签支持
 * @author huangxf
 * @date 2017年4月10日
 */
public class SimplePartnerSignServiceSupport extends AbstractPartnerSignService {
	
	private PartnerKeyRepository repository;

	public SimplePartnerSignServiceSupport(PartnerKeyRepository repository) {
		this.repository = repository;
	}

	protected boolean doCheckSign(Partner partner, SignType signType, String data,
			String signed) {
		StringBuilder builder = new StringBuilder( 100 );
		builder.append( data ).append( "&key=" ).append( getSignKey( partner ) );
		String sign = MD5Utils.md5( builder.toString() );
		return StringUtils.equalsIgnoreCase( sign, signed );
	}
	
	/**
	 * 判断是否支持验签
	 * @return boolean
	 */
	public boolean supportsSignCheck( Partner partner, SignType signType ) {
		if ( SignType.MD5 == signType ) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否支持签名
	 * @return boolean
	 */
	public boolean supportsDoSign( Partner partner, SignType signType ) {
		if ( SignType.MD5 == signType ) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取渠道对应的密钥，如果为空则抛出{@link SignException}
	 * @return String
	 */
	protected String getSignKey( Partner partner ) {
		String key = repository.getKey( partner );
		if ( StringUtils.isBlank( key ) ) {
			throw new SignException( MsgCode.CHANNEL_INVALID, "Partner key not found: " + partner.getPartnerId() );
		}
		return key;
	}

	@SuppressWarnings("rawtypes")
	protected Sign doInternalSign(Partner partner, PaymentResponse<?> response, SignType signType) {
		
		String signed = null;
		
		//如果是PaymentSignResponse则直接获取sign，若为空则另行加密处理
		if ( response instanceof PaymentSignResponse ) {
			signed = ((PaymentSignResponse) response).getSign();
			if ( StringUtils.isEmpty( signed ) ) {
				signed = sign( response.getData(), partner );
			}
		} else {
			signed = sign( response.getData(), partner );
		}
		
		return new SignObject( signed, signType );
		
	}
	
	protected String sign( Object data, Partner partner ) {
		String key = getSignKey( partner );
		String json = JsonUtils.toJson( data );
		StringBuilder builder = new StringBuilder( 100 );
		builder.append( json ).append( "&key=" ).append( key );
		return MD5Utils.md5( builder.toString() );
	}

	@Override
	public String getSignKey(String partnerId) {
		return null;
	}

}
