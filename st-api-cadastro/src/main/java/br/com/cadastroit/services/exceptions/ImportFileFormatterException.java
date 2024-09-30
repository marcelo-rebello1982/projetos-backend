package br.com.cadastroit.services.exceptions;

public class ImportFileFormatterException extends RuntimeException {

	private static final long serialVersionUID = 2803027068058406150L;

	public ImportFileFormatterException(String message) {

		super(message);
	}

	public ImportFileFormatterException(String message, Throwable e) {

		super(message, e);
	}

}