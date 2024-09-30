package br.com.complianceit.services.model;

import java.io.Serializable;

public class CallProcedureModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String pkg;
	private String type;

	public CallProcedureModel() {
	}

	public CallProcedureModel(Long id, String pkg, String type) {
		this.id = id;
		this.pkg = pkg;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}