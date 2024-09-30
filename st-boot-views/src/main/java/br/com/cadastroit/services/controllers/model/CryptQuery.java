package br.com.cadastroit.services.controllers.model;

import java.io.Serializable;

public class CryptQuery implements Serializable{

	private static final long serialVersionUID = -8133399170255161113L;
	private String qs;
	private String cryptQuery;
	
	public String getQs() {
		return qs;
	}
	public void setQs(String qs) {
		this.qs = qs;
	}
	public String getCryptQuery() {
		return cryptQuery;
	}
	public void setCryptQuery(String cryptQuery) {
		this.cryptQuery = cryptQuery;
	}
}
