package net.dwade.core.sign.internal;

import net.dwade.core.Partner;
import net.dwade.core.sign.PartnerSign;
import net.dwade.core.sign.SignType;

/**
 * 
 * @author huangxf
 * @date 2017年4月19日
 */
public class PartnerSignObject extends SignObject implements PartnerSign, Partner {

	private static final long serialVersionUID = 677732060725139012L;
	
	private String partnerId;
	
	/**
	 * @param partnerId
	 */
	public PartnerSignObject(String partnerId) {
		super();
		this.partnerId = partnerId;
	}

	public PartnerSignObject( String sign, SignType signType, String partnerId ) {
		super( sign, signType );
	}

	@Override
	public String getPartnerId() {
		return this.partnerId;
	}

}
