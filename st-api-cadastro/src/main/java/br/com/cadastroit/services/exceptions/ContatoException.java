package br.com.cadastroit.services.exceptions;

public class ContatoException extends GenericException {

	private static final long serialVersionUID = -5105103518678120961L;

	public ContatoException(String message) {

		super(message);
	}

	public ContatoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
