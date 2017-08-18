package net.dwade.core.support;

import net.dwade.core.PaymentResponse;
import net.dwade.core.PaymentSignResponse;
import net.dwade.core.sign.Sign;

/**
 * SignResponse适配器
 * @author huangxf
 * @date 2017年4月10日
 */
public class SignResponseAdapter<T> implements PaymentSignResponse<T> {
	
	private static final long serialVersionUID = 6894337779338637168L;

	private PaymentResponse<T> delegate;
	
	private Sign sign;

	/**
	 * @param delegate
	 * @param signSupport
	 */
	public SignResponseAdapter( PaymentResponse<T> delegate, Sign sign ) {
		this.delegate = delegate;
		this.sign = sign;
	}

	@Override
	public String getRetMsg() {
		return delegate.getRetMsg();
	}

	@Override
	public String getRetCode() {
		return delegate.getRetCode();
	}

	@Override
	public T getData() {
		return delegate.getData();
	}

	@Override
	public boolean isSuccess() {
		return delegate.isSuccess();
	}

	@Override
	public String getSign() {
		return sign.getSign();
	}

}
