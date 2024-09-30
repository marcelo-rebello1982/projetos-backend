package br.com.cadastroit.services.exceptions;

public class PessoaNfsException extends NfServException {

	private static final long serialVersionUID = 8038711851933297435L;

	public PessoaNfsException(String message) {
		super(message);
	}

	public PessoaNfsException(String message, Throwable cause) {
		super(buildMessage(message, cause), cause);
	}
}
