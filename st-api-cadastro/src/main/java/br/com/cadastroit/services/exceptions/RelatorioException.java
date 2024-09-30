package br.com.cadastroit.services.exceptions;
public class RelatorioException extends RuntimeException {

	private static final long serialVersionUID = 1920904223970444123L;

	public RelatorioException(String message) {

		super(message);
	}

	public RelatorioException(String message, Throwable e) {

		super(message, e);
	}

}
