package br.com.cadastroit.services.exceptions;

public class PessoaException extends GenericException {

	private static final long serialVersionUID = 2273069135636362375L;

	public PessoaException(String message) {

		super(message);
	}

	public PessoaException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
