package br.com.cadastroit.services.exceptions;
public class StorageException extends RuntimeException {

	private static final long serialVersionUID = 6559609736338599693L;

	public StorageException(String message) {

		super(message);
	}

	public StorageException(String message, Throwable e) {

		super(message, e);
	}

}