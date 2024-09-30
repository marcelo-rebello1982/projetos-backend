package br.com.cadastroit.services.exceptions;

public class PedidoException extends GenericException {

	private static final long serialVersionUID = -2954346773838721203L;

	public PedidoException(String message) {

		super(message);
	}

	public PedidoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
