package br.com.cadastroit.services.exceptions;

public class InterNfsException extends NfServException {

	private static final long serialVersionUID = -778201073797773683L;

	public InterNfsException(String message) {
		super(message);
	}

	public InterNfsException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
