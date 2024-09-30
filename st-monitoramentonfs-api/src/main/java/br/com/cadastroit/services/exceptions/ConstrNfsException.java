package br.com.cadastroit.services.exceptions;

public class ConstrNfsException extends NfServException {

	private static final long serialVersionUID = -4590524107159795261L;

	public ConstrNfsException(String message) {
		super(message);
	}

	public ConstrNfsException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
