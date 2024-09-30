package br.com.cadastroit.services.exceptions;
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = -2283721369050395330L;

	public BusinessException(String message) {

		super(message);
	}

	public BusinessException(String message, Throwable e) {

		super(message, e);
	}

}
