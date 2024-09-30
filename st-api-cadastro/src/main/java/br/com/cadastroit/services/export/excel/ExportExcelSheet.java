package br.com.cadastroit.services.export.excel;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.cadastroit.services.utils.StringUtils;


@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportExcelSheet {


	public String sheetName() default StringUtils.EMPTY;

	public boolean autoSizeColumn() default false;

	public boolean ignoreNullValues() default false;

}
