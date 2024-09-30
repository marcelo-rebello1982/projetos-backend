package br.com.cadastroit.services.export.excel;

import br.com.cadastroit.services.exceptions.ImportFileFormatterException;

public interface ImportFileFormatter<TReturn> {

	@SuppressWarnings("unchecked")
	default TReturn format(Object value) throws ImportFileFormatterException {

		return (TReturn) value;
	}
}