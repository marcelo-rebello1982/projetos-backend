package br.com.cadastroit.services.exceptions;
public class AppMailException extends RuntimeException {

	private static final long serialVersionUID = 5260636187036416794L;

	public AppMailException(String message) {

		super(message);
	}

	public AppMailException(String message, Throwable e) {

		super(message, e);
	}

}