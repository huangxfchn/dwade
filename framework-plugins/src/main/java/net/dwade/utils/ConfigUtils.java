package net.dwade.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * 配置文件工具类，默认读取app.properties文件，使用其它配置文件参考，eg：
 * <p>String value = ConfigUtils.getConfig( "payment.properties" ).getString( "key" );</p>
 * <p></p>
 * @author huangxf
 * @date 2017年4月7日
 */
public abstract class ConfigUtils {
	
	private static ConcurrentMap<String, Configuration> configHolder = new ConcurrentHashMap<String, Configuration>();
	
    public static final String DEFAULT_PROPERTY = "app.properties";
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    /**
     * 支持指定编码的{@link PropertiesConfiguration}
     * @author huangxf
     * @date 2017年4月7日
     */
    public static class EncodedPropertiesConfiguration extends PropertiesConfiguration {
    	
    	private String encoding = DEFAULT_ENCODING;
    	
    	public EncodedPropertiesConfiguration( String fileName ) throws ConfigurationException {
    		this( fileName, DEFAULT_ENCODING );
    	}
    	
    	public EncodedPropertiesConfiguration( String fileName, String encoding ) throws ConfigurationException {
    		
    		super();
    		
    		this.encoding = encoding;
    		
    		// store the file name
            setFileName( fileName );

            // load the file with encoding.
            load();
    	}
    	
    	/**
    	 * 在调用load()的时候会调用该方法获取编码
    	 */
    	@Override
    	public String getEncoding() {
    		return this.encoding;
    	}
    	
    }

    /**
     * 根据file获取对应的{@link Configuration}实例 
     * @param file
     * @return Configuration
     */
    public static final Configuration getConfig( String file ) {
    	Configuration config = configHolder.get( file );
    	if ( config == null ) {
			try {
				config = new EncodedPropertiesConfiguration( file );
				configHolder.putIfAbsent( file, config );
			} catch (ConfigurationException e) {
				throw new RuntimeException( "File not found: " + file, e );
			}
        }
        return config;
    }
    
    /**
     * 获取{@link ConfigUtils#DEFAULT_PROPERTY}文件中对应的{@link Configuration}实例
     * @param key
     * @return String
     */
    public static final Configuration getDefault() {
    	return getConfig( DEFAULT_PROPERTY );
    }
    
    /**
     * 获取properties文件中所有的键值对
     * @author huangxf
     * @param file
     * @return Map<String,String>
     */
    public static Map<String, String> getKeyAndValues( String file ) {
    	Map<String, String> results = new HashMap<String, String>();
    	Configuration prop = getConfig( file );
    	Iterator<String> keys = prop.getKeys();
    	while ( keys.hasNext() ) {
    		String key = keys.next();
    		String value = prop.getString( key );
    		results.put( key, value );
    	}
    	return results;
    }
    
	public static String getString(String key) {
		return getDefault().getString( key );
	}

	public static String getString(String key, String defaultValue) {
		return getDefault().getString( key, defaultValue );
	}

	public static int getInt(String key) {
		return getDefault().getInt( key );
	}

	public static int getInt(String key, int defaultValue) {
		return getDefault().getInt( key, defaultValue );
	}

	public static float getFloat(String key) {
		return getDefault().getFloat( key );
	}

	public static float getFloat(String key, float defaultValue) {
		return getDefault().getFloat( key, defaultValue );
	}

	public static double getDouble(String key) {
		return getDefault().getDouble( key );
	}

	public static double getDouble(String key, double defaultValue) {
		return getDefault().getDouble( key, defaultValue );
	}

	public static boolean getBoolean(String key) {
		return getDefault().getBoolean( key );
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return getDefault().getBoolean( key, defaultValue );
	}
	
    
}
