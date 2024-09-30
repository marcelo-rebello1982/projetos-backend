package br.com.cadastroit.services.exceptions;

public class NotFoundException extends ExceptionBase {

	private static final long serialVersionUID = 5508371313913875778L;

	private Class<?> classEntity;

	private String query;

	public NotFoundException(Class<?> classEntity, String query) {

		super(ExceptionEnum.NOT_FOUND_EXCEPTION);

		this.classEntity = classEntity;
		this.query = query;
	}

	public Class<?> getClassEntity() {

		return classEntity;
	}

	public String getQuery() {

		return query;
	}

	public void setQuery(String query) {

		this.query = query;
	}
}
