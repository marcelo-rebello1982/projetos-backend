package br.com.cadastroit.services.exceptions;

public class FaturaNfsException extends NfServException {

	private static final long serialVersionUID = 4825659254344789605L;

	public FaturaNfsException(String message) {
		super(message);
	}

	public FaturaNfsException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
