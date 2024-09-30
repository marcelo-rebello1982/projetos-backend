package br.com.cadastroit.services.export.excel;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define propriedades da coluna para importação.
 *
 */
@Target({ FIELD, METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportTxtColumn {

	/**
	 * Ordem do dado na linha do txt com base no delimitador
	 */
	public int ordem() default 0;

	public Class<? extends ImportTxtFormatter<?>> formatter() default DEFAULT.class;

	/**
	 * Dummy class apenas para setar no formatter um default.
	 */
	static final class DEFAULT implements ImportTxtFormatter<Object> {
	}

}
