package br.com.cadastroit.services.exceptions;

public class VIllegalArgumentException extends java.lang.IllegalArgumentException {

	private static final long serialVersionUID = -8514103921710466512L;

	private String message;

	private Object[] args;

	public VIllegalArgumentException(String message, Object[] args) {

		this.message = message;
		this.args = args;
	}

	public String getMessage() {

		return message;
	}

	public void setMessage(String message) {

		this.message = message;
	}

	public Object[] getArgumentName() {

		return args;
	}

	public void setArgumentName(Object[] args) {

		this.args = args;
	}

}