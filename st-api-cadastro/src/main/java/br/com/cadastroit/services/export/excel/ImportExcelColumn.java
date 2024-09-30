package br.com.cadastroit.services.export.excel;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportExcelColumn {

	public String name() default "";

	public Class<? extends ImportExcelFormatter<?>> formatter() default DEFAULT.class;

	static final class DEFAULT implements ImportExcelFormatter<Object> {
	}

}
