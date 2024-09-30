package br.com.cadastroit.services.exceptions;

public class ParametroException extends GenericException {

	private static final long serialVersionUID = 7621841997767051934L;

	public ParametroException(String message) {

		super(message);
	}

	public ParametroException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
