package br.com.complianceit.services.model.commons.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface OracleField {

	String mapField() default "";
	Class<?> convertTo() default Object.class;
	Class<?> reference() default Object.class;
	String fReference() default "";
}
