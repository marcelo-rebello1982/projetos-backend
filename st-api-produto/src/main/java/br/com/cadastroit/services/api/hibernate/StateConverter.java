package br.com.cadastroit.services.api.hibernate;

import javax.persistence.AttributeConverter;

public class StateConverter implements AttributeConverter<Boolean, String> {
	
	 // https://thorben-janssen.com/implement-soft-delete-hibernate/

	@Override
	public String convertToDatabaseColumn(Boolean attribute) {

		return attribute ? "active" : "inactive";
	}

	@Override
	public Boolean convertToEntityAttribute(String dbData) {

		return dbData.equals("active");
	}

}