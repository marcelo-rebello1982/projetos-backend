package br.com.complianceit.services.model.commons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterIndex {
	
	String qName() default "";
	String oracleFilter() default "";
	String noSqlFilter() default "";
	String condition() default "eq";
	boolean leftPad() default false;
	char charPad() default '0';
	Class<?> converter() default Object.class;
	Class<?> refOracle() default Object.class;
}
