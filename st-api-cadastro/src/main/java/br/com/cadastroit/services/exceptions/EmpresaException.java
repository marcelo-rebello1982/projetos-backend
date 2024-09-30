package br.com.cadastroit.services.exceptions;

public class EmpresaException extends GenericException {

	private static final long serialVersionUID = -6405992364075835493L;

	public EmpresaException(String message) {

		super(message);
	}

	public EmpresaException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
