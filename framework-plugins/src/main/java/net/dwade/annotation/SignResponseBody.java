package net.dwade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.ResponseBody;

import net.dwade.core.sign.SignType;

/**
 * 用于产生响应时进行签名，和{@link ResponseBody}类似
 * @author huangxf
 * @date 2017年4月10日
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SignResponseBody {

	SignType signType() default SignType.MD5;
	
}
