package net.dwade.core.support;

import net.dwade.core.ServiceResponse;
import net.dwade.utils.BeanUtils;

/**
 * 便于后续扩展，以json的格式重写了toString()方法
 * @author huangxf
 * @date 2017年4月11日
 */
public class AbstractServiceResponse implements ServiceResponse {

	private static final long serialVersionUID = 595764150927917157L;
	
	@Override
	public String toString() {
		return BeanUtils.toString( this );
	}

}
