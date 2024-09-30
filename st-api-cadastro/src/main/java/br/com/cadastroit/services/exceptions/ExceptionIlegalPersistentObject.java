package br.com.cadastroit.services.exceptions;
public class ExceptionIlegalPersistentObject extends ExceptionBase {

	private static final long serialVersionUID = -1805276581385127022L;

	private String message;

	public ExceptionIlegalPersistentObject(String message) {
		super(ExceptionEnum.ILLEGAL_PERSISTENT_EXCEPTION);
		
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}