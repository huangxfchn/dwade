package net.dwade.plugins.spring.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import net.dwade.core.Constants;
import net.dwade.core.SessionUser;

/**
 * 解析Controller方法中的{@link SessionUser}入参
 * @author huangxf
 * @date 2017年4月13日
 */
public class SessionUserResolver implements HandlerMethodArgumentResolver {
	
	private Logger logger = LoggerFactory.getLogger( this.getClass() );

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return ClassUtils.isAssignable(  SessionUser.class, parameter.getParameterType() );
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpSession session = request.getSession();
		Object user = session.getAttribute( Constants.Session.USER_KEY );
		if ( user instanceof SessionUser ) {
			return user;
		} else {
			logger.error( "无法从Session中获取用户信息!" );
			throw new IllegalStateException( "Session中无用户信息!" );
		}
	}

}
