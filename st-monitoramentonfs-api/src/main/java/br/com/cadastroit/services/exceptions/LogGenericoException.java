package br.com.cadastroit.services.exceptions;

public class LogGenericoException extends NfServException {

	private static final long serialVersionUID = 5846227144897119550L;

	public LogGenericoException(String message) {
		super(message);
	}

	public LogGenericoException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
