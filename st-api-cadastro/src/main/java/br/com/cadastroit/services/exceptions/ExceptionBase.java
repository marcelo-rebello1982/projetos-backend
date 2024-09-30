package br.com.cadastroit.services.exceptions;
public abstract class ExceptionBase extends Exception {

	private static final long serialVersionUID = -9174772623106562296L;

	public ExceptionEnum exceptionEnum;
	
	public ExceptionBase(ExceptionEnum exceptionEnum){
		this.exceptionEnum = exceptionEnum;
	}

	public ExceptionEnum getExceptionEnum() {
		return exceptionEnum;
	}
}