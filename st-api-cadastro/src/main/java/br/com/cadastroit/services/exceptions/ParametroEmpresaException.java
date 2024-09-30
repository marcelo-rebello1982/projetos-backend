package br.com.cadastroit.services.exceptions;

public class ParametroEmpresaException extends GenericException {

	private static final long serialVersionUID = -8391757411427200443L;

	public ParametroEmpresaException(String message) {

		super(message);
	}

	public ParametroEmpresaException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
