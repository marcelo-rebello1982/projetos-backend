package br.com.cadastroit.services.exceptions;

public class ItemException extends GenericException {

	private static final long serialVersionUID = 2273069135636362375L;

	public ItemException(String message) {

		super(message);
	}

	public ItemException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
