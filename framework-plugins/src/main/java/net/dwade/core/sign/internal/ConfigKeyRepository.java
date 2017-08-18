package net.dwade.core.sign.internal;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dwade.core.Partner;
import net.dwade.core.sign.PartnerKeyRepository;
import net.dwade.utils.ConfigUtils;

/**
 * 以配置文件的方式获取渠道密钥，例如：
 * key#1000049=2210813e438782f61a716e14b98473dd
 * @author huangxf
 * @date 2017年4月10日
 */
public class ConfigKeyRepository implements PartnerKeyRepository {
	
	private static final Logger logger = LoggerFactory.getLogger( ConfigKeyRepository.class );
	
	public String CONFIG_NAME = "partner.properties";
	
	private Configuration config;
	
	public ConfigKeyRepository() {
		try {
			config = ConfigUtils.getConfig( CONFIG_NAME );
		} catch (Exception e) {
			logger.warn( "No partner.properties was found.", e );
		}
	}
	
	@Override
	public String getKey(Partner partner) {
		return config.getString( "key#" + partner.getPartnerId() );
	}

	@Override
	public void update(Partner partner, String key) {
		throw new UnsupportedOperationException( "Don't be allowed to update." );
	}

	@Override
	public void save(Partner partner, String key) {
		throw new UnsupportedOperationException( "Don't be allowed to save." );
	}

	@Override
	public void remove(Partner partner) {
		throw new UnsupportedOperationException( "Don't be allowed to remove." );
	}

}
