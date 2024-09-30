package br.com.cadastroit.services.exceptions;

public class PessoaEmpresaException extends GenericException {

	private static final long serialVersionUID = -7734771155608951220L;

	public PessoaEmpresaException(String message) {

		super(message);
	}

	public PessoaEmpresaException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
