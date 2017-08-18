package net.dwade.core;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import net.dwade.core.exception.BaseException;
import net.dwade.utils.ConfigUtils;

/**
 * 如果子类要继承，需要在META-INF/services/net.dwade.core.MsgCode文件中指定类名，
 * {@link MsgCode#init()}会加载子类，这样才可以执行子类的static代码块
 * @author huangxf
 * @date 2017年4月8日
 */
public abstract class MsgCode {
	
	private static final Logger logger = LoggerFactory.getLogger( MsgCode.class );
	
	protected static final String LOCATION = "net/dwade/core/message.properties";
	
	protected static final ConcurrentMap<String, String> ERR_MSGS = new ConcurrentHashMap<String, String>( 64 );
	
	@SuppressWarnings("rawtypes")
	private static final Map<Class, String> EXCEPTION_TO_CODE = new ConcurrentHashMap<Class, String>( 32 );
	
	private static final String SPRING_DATA_ACCESS_EX_CLASS = "org.springframework.dao.DataAccessException";
	
	/**
	 * spring-tx会把SQL异常转换成DataAccessException
	 */
	private static final boolean SPRING_DATA_PRESENT = ClassUtils.isPresent( SPRING_DATA_ACCESS_EX_CLASS, null );
	
	/** 成功 */
	public static final String SUCCESS = "000000";
	
	/** 未知错误 */
	public static final String ERROR = "999999";
	
	/** @see RuntimeException */
	public static final String RUNTIME_EXCEPTION = "030000";
	
	/** 数据访问层抛出的异常 */
	public static final String DAO_EXCEPTION = "040000";
	
	/** SQLException */
	public static final String SQL_EXCEPTION = "040001";
	
	/** 系统异常,错误码请联系管理员 */
	public static final String SERVICE_EORROR = "000001";
	
	public static final String SERVICE_INFO = "000011";
	
	/** 无结果 */
	public static final String NO_RESULT = "000003";
	
	/** 参数校验失败 */
	public static final String ERROR_PARAM_CODE = "000002";
	
	/** 空指针异常 */
	public static final String NULL_POINT_EXCEPTION = "000050";
	
	/** IO异常 */
	public static final String IO_EXCEPTION = "000100";
	
	/** 数学运算异常 */
	public static final String ARITHMETIC_EXCEPTION = "000210";
	
	/** 数组下标越界 */
	public static final String ARRAY_INDEX_OUT_EXCEPTION = "000211";
	
	/** 参数错误 */
	public static final String ILLEGAL_ARGS_EXCEPTION = "000212";
	
	/** 类型转换异常 */
	public static final String CLASS_CAST_EXCEPTION = "000213";
	
	/** 违背安全原则异常 */
	public static final String SECURITY_EXCEPTION = "000214";
	
	/** JVM内部错误 */
	public static final String INTERVAL_EXCEPTION = "000216";
	
	/** 解析异常 */
	public static final String PARSE_EXCEPTION = "000217";
	
	/** AES加密、解密异常 */
	public static final String AES_EXCEPTION = "000218";
	
	/** 接口参数校验通用错误 */
	public static final String PARAM_IS_NULL = "000300";

	/** 请求时间不正确 */
	public static final String TIMESTAMP_INVALID = "010010";
	
	/** 渠道标识不匹配 */
	public static final String CHANNEL_INVALID = "010011";
	
	/** 数据格式不匹配 */
	public static final String FORMAT_INVALID = "010012";
	
	/** 签名解码不正确 */
	public static final String SIGN_DECODE_FAIL = "010013";
	
	/** 签名不匹配 */
	public static final String SIGN_INVALID = "010016";
	
	/** 不支持的签名类型 */
	public static final String SIGN_UNSUPPORTED = "010017";
	
	/**
	 * 签名校验出现异常
	 */
	public static final String SIGN_CHECK_EXCEPTION = "010018";
	
	/**
	 * 签名异常，无data数据
	 */
	public static final String SIGN_NO_DATA = "010019";
	
	/**
	 * 签名异常，无法获取partnerId
	 */
	public static final String SIGN_NO_PARTNER = "010020";

	/** 020000--029999 通用dubbo层错误信息 */
	public static final String DUBBO_RUNTIME_EXCEPTION = "020000";
	
	static {
		init();
		loadProperties();
		bindCode();
	}
	
	/**
	 * 加载子类，以便执行
	 * @author huangxf
	 * @return void
	 */
	private static void init() {
		Iterator<MsgCode> it = ServiceLoader.load( MsgCode.class ).iterator();
		while ( it.hasNext() ) {
			MsgCode msgCode = it.next();
			logger.info( "loaded class:" + msgCode.getClass() );
		}
	}
	
	/**
	 * 将异常类名，绑定具体的错误编码 
	 * @return void
	 */
	protected static void bindCode() {
		if ( SPRING_DATA_PRESENT ) {
			try {
				EXCEPTION_TO_CODE.put( ClassUtils.forName( SPRING_DATA_ACCESS_EX_CLASS, null ), MsgCode.DAO_EXCEPTION );
			} catch (Exception e) {
				// ignore exception
			}
		}
		EXCEPTION_TO_CODE.put( ReflectiveOperationException.class, MsgCode.INTERVAL_EXCEPTION );
		EXCEPTION_TO_CODE.put( NullPointerException.class, MsgCode.NULL_POINT_EXCEPTION );
		EXCEPTION_TO_CODE.put( IOException.class, MsgCode.IO_EXCEPTION );
		EXCEPTION_TO_CODE.put( IllegalArgumentException.class, MsgCode.ILLEGAL_ARGS_EXCEPTION );
		EXCEPTION_TO_CODE.put( ClassCastException.class, MsgCode.CLASS_CAST_EXCEPTION );
		EXCEPTION_TO_CODE.put( RuntimeException.class, MsgCode.RUNTIME_EXCEPTION );
		EXCEPTION_TO_CODE.put( SQLException.class, MsgCode.SQL_EXCEPTION );
		EXCEPTION_TO_CODE.put( Throwable.class, MsgCode.ERROR );
	}

	/**
	 * 初始化错误信息，从message.properties文件中读取错误信息映射关系
	 * @param 资源文件名字
	 * @return Map<String,String> 信息集合
	 */
	private static void loadProperties() {
		Map<String, String> keyValues = ConfigUtils.getKeyAndValues( LOCATION );
		ERR_MSGS.putAll( keyValues );
	}
	
	public static boolean isSuccess( String code ) {
		return StringUtils.equals( code, SUCCESS );
	}

	/**
	 * 根据错误编码得到错误信息
	 * 
	 * @param 错误编码
	 * @return 错误信息<String>
	 */
	public static String getMsg(String code) {
		String str = ERR_MSGS.get(code);
		if (str == null) {
			return StringUtils.EMPTY;
		}
		return str;
	}

	/**
	 * 根据错误编码得到错误信息
	 * 
	 * @param 错误编码
	 * @param 动态参数
	 *            用于给{0}..赋值
	 * @return 错误信息<String>
	 */
	public static String getMsg(String code, Object... args) {
		String str = getMsg(code);
		if (str == null) {
			return StringUtils.EMPTY;
		}
		try {
			return MessageFormat.format(str, args);
		} catch (Exception e) {
			logger.warn( "MsgCode#getMsg() error, return null.", e );
			return StringUtils.EMPTY;
		}
	}

	/**
	 * 根据异常类得到 相应的错误编码
	 */
	public static String getCode( Throwable e ) {
		if ( e == null ) {
			return null;
		}
		if ( e instanceof BaseException ) {
			return ((BaseException) e).getErrorCode();
		}
		Class<?> clazz = e.getClass();
		while ( clazz != Object.class ) {
			e.getClass().getSuperclass();
			String code = EXCEPTION_TO_CODE.get( clazz );
			if ( code != null ) {
				return code;
			}
			clazz = clazz.getSuperclass();
		}
		return ERROR;
	}
	
	/**
	 * @see MsgCode#PARAM_IS_NULL
	 * @author huangxf
	 * @return String
	 */
	public static String paramErrorMsg() {
		return MsgCode.getMsg( MsgCode.PARAM_IS_NULL );
	}
	
	/**
	 * @see MsgCode#PARAM_IS_NULL
	 * @author huangxf
	 * @return String
	 */
	public static String paramErrorMsg( Object... args ) {
		return MsgCode.getMsg( MsgCode.PARAM_IS_NULL, args );
	}
	
}
