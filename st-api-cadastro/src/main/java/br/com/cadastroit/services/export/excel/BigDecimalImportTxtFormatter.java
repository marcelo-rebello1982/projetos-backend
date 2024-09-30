package br.com.cadastroit.services.export.excel;

import java.math.BigDecimal;

import br.com.cadastroit.services.exceptions.ImportFileFormatterException;

public class BigDecimalImportTxtFormatter extends BigDecimalImportFileFormatter implements ImportTxtFormatter<BigDecimal> {

	@Override
	public BigDecimal format(Object value) throws ImportFileFormatterException {

		return super.format(value);
	}

}