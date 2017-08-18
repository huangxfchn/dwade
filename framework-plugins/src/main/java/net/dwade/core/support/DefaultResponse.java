package net.dwade.core.support;

import net.dwade.core.MsgCode;
import net.dwade.core.PaymentResponse;
import net.dwade.utils.BeanUtils;

/**
 * 默认的{@link PaymentResponse}实现类
 * @author huangxf
 * @date 2017年4月10日
 */
public class DefaultResponse<T> implements PaymentResponse<T>, 
	java.io.Serializable {
	
	private static final long serialVersionUID = 2443652247345813873L;

	protected String retCode = MsgCode.SUCCESS;
	
	protected String retMsg = MsgCode.getMsg( retCode );
	
	protected boolean success;
	
	private T data;
	
	public DefaultResponse() {	}
	
	public DefaultResponse( T data ) {
		this.data = data;
	}
	
	/**
	 * @param retCode
	 * @param retMsg
	 */
	public DefaultResponse(String retCode, String retMsg) {
		super();
		this.retCode = retCode;
		this.retMsg = retMsg;
	}

	/**
	 * @param retCode
	 * @param retMsg
	 * @param data
	 */
	public DefaultResponse(String retCode, String retMsg, T data) {
		this.retCode = retCode;
		this.retMsg = retMsg;
		this.data = data;
	}

	@Override
	public String getRetMsg() {
		return this.retMsg;
	}

	@Override
	public String getRetCode() {
		return this.retCode;
	}

	@Override
	public boolean isSuccess() {
		return MsgCode.isSuccess( this.retCode );
	}

	@Override
	public T getData() {
		return this.data;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public void setRetMsg(String retMsg) {
		this.retMsg = retMsg;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	/**
	 * this.getClass().getSimpleName() + " [" + json  + " ]"
	 */
	@Override
	public String toString() {
		return BeanUtils.toString( this );
	}

}
