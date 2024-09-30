package br.com.cadastroit.services.exceptions;
public class VUtilsRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 3511095404470427362L;

	public VUtilsRuntimeException(String message) {

		super(message);
	}

	public VUtilsRuntimeException(String message, Throwable cause) {

		super(message, cause);
	}

}