package net.dwade.core.sign.internal;

import org.apache.commons.lang3.StringUtils;

import net.dwade.core.sign.Sign;
import net.dwade.core.sign.SignType;

/**
 * {@link Sign}实现类
 * @author huangxf
 * @date 2017年4月10日
 */
public class SignObject implements Sign, java.io.Serializable {
	
	private static final long serialVersionUID = 3913186518959579523L;

	private String sign;
	
	private SignType signType;
	
	private String data;
	
	public SignObject() {
		this( StringUtils.EMPTY, SignType.MD5 );
	}

	/**
	 * @param sign
	 * @param signType
	 */
	public SignObject(String sign, SignType signType) {
		super();
		this.sign = sign;
		this.signType = signType;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public SignType getSignType() {
		return signType;
	}

	public void setSignType(SignType signType) {
		this.signType = signType;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SignObject [sign=" + sign + ", signType=" + signType
				+ ", data=" + data + "]";
	}

}
