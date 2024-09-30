package br.com.cadastroit.services.exceptions;

public class ProcessaPedidoException extends GenericException {

	private static final long serialVersionUID = -3468892784165018618L;

	public ProcessaPedidoException(String message) {

		super(message);
	}

	public ProcessaPedidoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
