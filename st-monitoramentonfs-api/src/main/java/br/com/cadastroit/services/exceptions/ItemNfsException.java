package br.com.cadastroit.services.exceptions;

public class ItemNfsException extends NfServException {

	private static final long serialVersionUID = 7354321495322502160L;

	public ItemNfsException(String message) {
		super(message);
	}

	public ItemNfsException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
