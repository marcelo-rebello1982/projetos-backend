package br.com.cadastroit.services.exceptions;

public class ItemPedidoException extends GenericException {

	private static final long serialVersionUID = 4700987789419854684L;

	public ItemPedidoException(String message) {

		super(message);
	}

	public ItemPedidoException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
