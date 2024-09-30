package br.com.cadastroit.services.exceptions;

public class MongoDBException extends GenericException {

	private static final long serialVersionUID = 7457698827524471711L;

	public MongoDBException(String message) {

		super(message);
	}

	public MongoDBException(String message, Throwable cause) {

		super(buildMessage(message, cause), cause);
	}
}
