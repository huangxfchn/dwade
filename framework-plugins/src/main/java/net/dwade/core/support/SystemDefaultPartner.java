package net.dwade.core.support;

/**
 * 系统默认的商户渠道，主要用于验签、加密
 * @author huangxf
 * @date 2017年4月19日
 */
public class SystemDefaultPartner extends PartnerObject {
	
	public static final String DEFAULT = "10000";
	
	public SystemDefaultPartner() {
		super( DEFAULT, "System" );
	}

	@Override
	public String getPartnerId() {
		return DEFAULT;
	}

	@Override
	public void setPartnerId(String partnerId) {
		throw new UnsupportedOperationException( "Not allowed to set partnerId for DefaultPartner" );
	}

	@Override
	public String toString() {
		return "DefaultPartner [getPartnerId=" + DEFAULT + "]";
	}

}
