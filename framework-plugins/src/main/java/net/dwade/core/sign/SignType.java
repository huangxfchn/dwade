package net.dwade.core.sign;

import org.apache.commons.lang3.StringUtils;

/**
 * 签名类型
 * @author huangxf
 * @date 2017年4月10日
 */
public enum SignType {
	
	MD5( "MD5" ),
	
	AES( "AES" );
	
	private final String type;
	
	private SignType( String type ) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	/**
	 * 根据指定的String返回枚举（忽略大小写）
	 * @return SignType
	 */
	public static SignType forName( String type ) {
		for ( SignType sign : SignType.values() ) {
			if ( StringUtils.equalsAnyIgnoreCase( type, sign.name() ) ) {
				return sign;
			}
		}
		return null;
	}
	
}
