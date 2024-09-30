package br.com.cadastroit.services.exceptions;
public class StorageFileNotFoundException extends StorageException {

	private static final long serialVersionUID = -567361728107112881L;

	public StorageFileNotFoundException(String message) {

		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable e) {

		super(message, e);
	}

}
