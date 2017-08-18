package net.dwade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Form表单提交的注解，不包括ajax方式提交表单
 * @author huangxf
 * @date 2017年5月6日
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormSubmit {
	
	/**
	 * 表单提交后是否重定向，默认是true
	 * @return boolean
	 */
	boolean redirect() default true;
	
	/**
	 * 操作成功之后转发或者重定向的URL，是否重定向由{@link #redirect()}决定 
	 * @return String
	 */
	String success() default "redirect:/success.html";
	
	/**
	 * 操作失败之后转发或者重定向的URL，是否重定向由{@link #redirect()}决定 
	 * @return String
	 */
	String failed() default "redirect:/failure.html";
	
	/**
	 * 重定向之后的返回链接 
	 * @return String
	 */
	String back() default "";
	
}
