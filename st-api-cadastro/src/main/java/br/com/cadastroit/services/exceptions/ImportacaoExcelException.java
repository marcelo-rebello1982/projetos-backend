package br.com.cadastroit.services.exceptions;

public class ImportacaoExcelException extends RuntimeException {

	private static final long serialVersionUID = -7756580940988344521L;

	public ImportacaoExcelException(String message) {

		super(message);
	}

	public ImportacaoExcelException(String message, Throwable e) {

		super(message, e);
	}

}