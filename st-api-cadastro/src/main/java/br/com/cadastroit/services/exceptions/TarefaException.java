package br.com.cadastroit.services.exceptions;

public class TarefaException extends GenericException {

	private static final long serialVersionUID = 288696900272494772L;

	public TarefaException(String message) {

		super(message);
	}

	public TarefaException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
