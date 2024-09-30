package br.com.cadastroit.services.exceptions;

public class DepartamentoException extends GenericException {

	private static final long serialVersionUID = 2146494304136417148L;

	public DepartamentoException(String message) {

		super(message);
	}

	public DepartamentoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
