package br.com.cadastroit.services.export.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Calendar;

import br.com.cadastroit.services.utils.StringUtils;


@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportExcelColumn {

	/**
	 * Nome/cabeçalho da coluna da planilha.
	 */
	public String name();

	/**
	 * Formato de exibição da data, caso o tipo do campo seja {@link Date} ou {@link Calendar}. Pode ser utilizado patterns
	 * definidos em {@link DateUtils}.<br>
	 * Formato padrão: dd/MM/yyyy
	 */
	public String dateFormat() default StringUtils.EMPTY;

	/**
	 * Ordenação do campo na planilha. <br>
	 * Utilizar apenas se a ordem de definição dos campos na classe não for a desejada para o arquivo.<br>
	 * <br>
	 * <i>Sugestão: utilizar valores múltiplos de 10 ao definir ordenação para, se necessário, adicionar novo campo entre os
	 * existentes.</i>
	 */
	public int order() default 0;

	/**
	 * Para habilitar quebra de linha na coluna <br>
	 * <i>É necessário que o texto possua o carácter responsável pela quebra de linha(“\n”, “\r” ou “\r\n”)</i>
	 * 
	 */
	public boolean wrapText() default false;

}
