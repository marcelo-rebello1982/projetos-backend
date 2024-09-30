package br.com.cadastroit.services.exceptions;

public class EnderecoException extends GenericException {

	private static final long serialVersionUID = -6405992364075835493L;

	public EnderecoException(String message) {

		super(message);
	}

	public EnderecoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
