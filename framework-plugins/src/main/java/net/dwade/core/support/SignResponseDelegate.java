package net.dwade.core.support;

import net.dwade.core.PaymentSignResponse;

/**
 * SignResponse的委托模式
 * @author huangxf
 * @date 2017年5月5日
 */
public class SignResponseDelegate<T> implements PaymentSignResponse<T> {

	private static final long serialVersionUID = 8976547628523378004L;
	
	PaymentSignResponse<T> delegate;
	
	public SignResponseDelegate( PaymentSignResponse<T> delegate ) {
		this.delegate = delegate;
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
		return delegate.getSign();
	}
	
	public PaymentSignResponse<T> getDelegate() {
		return this.delegate;
	}

}
