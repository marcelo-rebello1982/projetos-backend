package br.com.cadastroit.services.exceptions;

public class PensamentoException extends GenericException {

	private static final long serialVersionUID = -6837945812404049860L;

	public PensamentoException(String message) {

		super(message);
	}

	public PensamentoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
