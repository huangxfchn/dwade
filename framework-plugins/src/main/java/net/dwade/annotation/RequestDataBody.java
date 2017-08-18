package net.dwade.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 用于扩展SpringMVC的参数解析功能，只读取json串中的data节点作为Controller方法入参，eg:
 * public JsonResult pay( @RequestDataBody PayRequest request, Sign sign )
 * @author huangxf
 * @date 2017年4月10日
 */
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestDataBody {

	boolean required() default true;
	
}
