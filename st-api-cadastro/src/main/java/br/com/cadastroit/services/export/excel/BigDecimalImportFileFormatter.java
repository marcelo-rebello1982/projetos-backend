package br.com.cadastroit.services.export.excel;

import java.math.BigDecimal;

import br.com.cadastroit.services.exceptions.ImportFileFormatterException;
import br.com.cadastroit.services.utils.StringUtils;

public class BigDecimalImportFileFormatter implements ImportFileFormatter<BigDecimal> {

	@Override
	public BigDecimal format(Object value) throws ImportFileFormatterException {

		if (value == null)
			return null;

		BigDecimal returnValue = null;
		String valueAsString = value.toString();

		if (StringUtils.isNotBlank(valueAsString))
			try {

				String str = valueAsString.trim();

				if (str.contains(StringUtils.SYMBOL_COMMA))
					str = str.replace(StringUtils.SYMBOL_DOT, StringUtils.EMPTY).replace(StringUtils.SYMBOL_COMMA, StringUtils.SYMBOL_DOT);

				if (str.contains(StringUtils.SYMBOL_RS))
					str = str.replace(StringUtils.SYMBOL_RS, StringUtils.EMPTY);

				str = str.replaceAll("\\s", StringUtils.EMPTY);

				returnValue = new BigDecimal(str);

			} catch (Exception e) {
				throw new ImportFileFormatterException(
						"Não foi possível obter o valor numérico data através do valor informado. Valor informado: " + valueAsString);
			}

		return returnValue;

	}

}
