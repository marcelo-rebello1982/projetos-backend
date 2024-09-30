package br.com.cadastroit.services.exceptions;

public class CommonsApiException extends GenericException {

	private static final long serialVersionUID = -6405992364075835493L;

	public CommonsApiException(String message) {

		super(message);
	}

	public CommonsApiException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
