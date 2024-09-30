package br.com.complianceit.services.exceptions;

public class CommonsApiException extends RuntimeException{

	private static final long serialVersionUID = -6574855979404411848L;

	public CommonsApiException() {
		super();
	}

	public CommonsApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommonsApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommonsApiException(String message) {
		super(message);
	}

	public CommonsApiException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
