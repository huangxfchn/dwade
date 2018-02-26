package net.dwade.plugins.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dwade.core.PaymentResponse;
import net.dwade.core.support.ResponseHelper;
import net.dwade.utils.JsonUtils;

/**
 * 出现异常时，响应400状态码，并返回包装的异常信息
 * @author huangxf
 * @date 2017年4月29日
 */
public class PaymentExceptionHandler implements ExceptionMapper<Throwable> {
	
	private static final Logger logger = LoggerFactory.getLogger( PaymentExceptionHandler.class );

	@Override
	public Response toResponse( Throwable exception ) {
		
		logger.error( exception.getMessage(), exception );
		
		//转换异常
		PaymentResponse<Void> response = ResponseHelper.errorResponse( exception );
		String text = JsonUtils.toJson( response );
		
		return Response.status(Status.OK).entity(text).build();
	}

}
