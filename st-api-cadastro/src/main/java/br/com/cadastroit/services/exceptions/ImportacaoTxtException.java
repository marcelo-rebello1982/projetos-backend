package br.com.cadastroit.services.exceptions;

public class ImportacaoTxtException extends RuntimeException {

	private static final long serialVersionUID = -9207018115794809377L;

	public ImportacaoTxtException(String message) {

		super(message);
	}

	public ImportacaoTxtException(String message, Throwable e) {

		super(message, e);
	}

}