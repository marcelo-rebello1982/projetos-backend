package br.com.complianceit.services.model.nosql;

import java.io.Serializable;

import br.com.complianceit.enterprise.common.nosql.annotations.Document;

@Document(value="nfeapi-history-filter")
public class NfeApiHistoryFilter implements Serializable{
	
	private static final long serialVersionUID = 8161988703347818200L;
	private Long id;
	private Long empresaId;
	private String token;
	private String filters;
	private Long timeout;
	private String message;
	private String typeCompany;
	private String urlS3;
	private String tDoc;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEmpresaId() {
		return empresaId;
	}
	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getFilters() {
		return filters;
	}
	public void setFilters(String filters) {
		this.filters = filters;
	}
	public Long getTimeout() {
		return timeout;
	}
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getTypeCompany() {
		return typeCompany;
	}
	public void setTypeCompany(String typeCompany) {
		this.typeCompany = typeCompany;
	}
	public String getUrlS3() {
		return urlS3;
	}
	public void setUrlS3(String urlS3) {
		this.urlS3 = urlS3;
	}
	public String gettDoc() {
		return tDoc;
	}
	public void settDoc(String tDoc) {
		this.tDoc = tDoc;
	}
}
