package br.com.cadastroit.services.exceptions;

public class TelefoneException extends GenericException {

	private static final long serialVersionUID = 2642419316817214130L;

	public TelefoneException(String message) {

		super(message);
	}

	public TelefoneException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
