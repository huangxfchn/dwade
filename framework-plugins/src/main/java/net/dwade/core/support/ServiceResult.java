package net.dwade.core.support;

import net.dwade.core.MsgCode;
import net.dwade.core.exception.BaseException;
import net.dwade.utils.JsonUtils;

/**
 * 带有状态标识的Service出参，用于某些需要对返回业务参数需要给出成功与否的标识，
 * 或者某些不必要抛出ServiceException的service方法，比如登录接口，但是像关键
 * 参数校验失败，还是建议在方法中直接抛出异常
 * @author huangxf
 * @date 2017年4月20日
 */
public class ServiceResult extends AbstractServiceResponse {

	private static final long serialVersionUID = 4453887808803473307L;
	
	protected boolean success = true;
	
	protected String retCode = MsgCode.SUCCESS;
	
	protected String retMsg = "success";
	
	/**
	 * 标识是否对retMsg进行设值
	 */
	private boolean ifMarkMsg = false;
	
	public ServiceResult() {
		super();
	}
	
	public ServiceResult( boolean success ) {
		this.success = success;
	}
	
	public static ServiceResult build() {
		return new ServiceResult();
	}
	
	public static ServiceResult build(boolean success) {
		return new ServiceResult( success );
	}
	
	public static ServiceResult build( boolean success, String retCode ) {
		return ServiceResult.build().mark( success ).markCode( retCode );
	}
	
	public static ServiceResult build( boolean success, String retCode, String retMsg ) {
		return ServiceResult.build().mark( success ).markCode( retCode ).markMsg( retMsg );
	}

	/**
	 * @param retCode
	 * @param retMsg
	 */
	public ServiceResult(String retCode, String retMsg) {
		this.retCode = retCode;
		this.retMsg = retMsg;
	}
	
	/**
	 * 标记状态码
	 * @return ServiceResponseMarker
	 */
	public ServiceResult markCode( String code ) {
		this.retCode = code;
		return this;
	}
	
	/**
	 * @return ServiceResponseMarker
	 */
	public ServiceResult markMsg( String msg ) {
		this.retMsg = msg;
		this.ifMarkMsg = true;
		return this;
	}
	
	/**
	 * 标记成功or失败
	 * @return ServiceResponseMarker
	 */
	public ServiceResult mark( boolean success ) {
		this.success = success;
		return this;
	}
	
	public ServiceResult mark( ServiceResult result ) {
		this.success = result.success;
		this.retCode = result.retCode;
		this.retMsg = result.retMsg;
		return this;
	}
	
	/**
	 * 使用异常标记retCode、retMsg
	 */
	public ServiceResult mark( Throwable e ) {
		this.success = false;
		if ( e instanceof BaseException ) {
			this.retCode = ((BaseException)e).getErrorCode();
			this.retMsg = ((BaseException)e).getErrorMsg();
		} else {
			this.retCode = MsgCode.getCode( e );
			this.retMsg = MsgCode.getMsg( this.retCode );
		}
		return this;
	}
	
	/**
	 * 返回code
	 * @return String
	 */
	public String retCode() {
		return this.retCode;
	}
	
	/**
	 * 如果标记了msg则返回设置的msg，如果未设置，并且是非success的，则返回MsgCode中code对应的msg
	 * 因为MsgCode继承的子类会把code对应的msg添加到父类的MsgCode中，即便是子类定义的code也可以取到msg
	 * @return String
	 */
	public String retMsg() {
		if ( ifMarkMsg ) {
			return this.retMsg;
		}
		if ( !success ) {
			return MsgCode.getMsg( retCode );
		}
		return this.retMsg;
	}
	
	public boolean success() {
		return this.success;
	}
	
	private static final char SEPARATOR = '-';
	
	private static final char DOT = ',';
	
	/**
	 * toString输出格式：className-success,retCode,retMsg[json]
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder( 64 );
		builder.append( this.getClass().getSimpleName() ).append( SEPARATOR ).append( this.success );
		builder.append( DOT ).append( this.retCode() ).append( DOT ).append( this.retMsg() );
		builder.append( "[" ).append( JsonUtils.toJson( this ) ).append( "]" );
		return builder.toString();
	}

}
