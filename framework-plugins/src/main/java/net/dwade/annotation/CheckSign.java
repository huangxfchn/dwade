package net.dwade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dwade.core.sign.SignType;

/**
 * 用于签名校验
 * @author huangxf
 * @date 2017年4月10日
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckSign {
	
	/**
	 * 签名的类型，默认MD5
	 * @return String
	 */
	SignType signType() default SignType.MD5;
	
	/**
	 * 校验签名的beanName
	 * @return String
	 */
	String beanName() default "partnerSignService";

}
