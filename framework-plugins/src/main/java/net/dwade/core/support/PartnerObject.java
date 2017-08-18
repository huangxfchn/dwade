package net.dwade.core.support;

import net.dwade.core.Partner;

public class PartnerObject implements Partner {
	
	private String partnerId;
	
	private String partnerName;
	
	/**
	 * 
	 */
	public PartnerObject() {
		super();
	}

	/**
	 * @param partnerId
	 * @param partnerName
	 */
	public PartnerObject(String partnerId, String partnerName) {
		super();
		this.partnerId = partnerId;
		this.partnerName = partnerName;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	@Override
	public String toString() {
		return "PartnerObject [partnerId=" + partnerId + ", partnerName="
				+ partnerName + "]";
	}

}
