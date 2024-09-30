package br.com.cadastroit.services.export;

import java.util.Calendar;
import java.util.Date;

public class ExportColumnDTO {

	private Object value;
	private String dateFormat;
	private int order;

	protected ExportColumnDTO(Object value, String dateFormat, int order) {

		this.value = value;
		this.dateFormat = dateFormat;
		this.order = order;
	}

	public Object getValue() {

		return value;
	}

	public void setValue(Object value) {

		this.value = value;
	}

	public String getDateFormat() {

		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {

		this.dateFormat = dateFormat;
	}

	public int getOrder() {

		return order;
	}

	public void setOrder(int order) {

		this.order = order;
	}

	public boolean isValueDate() {

		return this.hasValue() && (this.getValue() instanceof Calendar || this.getValue() instanceof Date);
	}

	public boolean hasValue() {

		return this.getValue() != null;
	}

}
