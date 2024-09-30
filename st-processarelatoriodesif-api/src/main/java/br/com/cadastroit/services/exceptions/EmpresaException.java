package br.com.cadastroit.services.exceptions;

public class EmpresaException extends DesifPlanoContaException {

	private static final long serialVersionUID = -1941927729388676456L;

	public EmpresaException(String message) {
		super(message);
	}

	public EmpresaException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
