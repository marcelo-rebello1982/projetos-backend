package br.com.complianceit.services.commons.flux;

import java.sql.Connection;
import java.util.HashMap;

import br.com.complianceit.services.builder.ResponseBuilder;
import br.com.complianceit.services.model.auth.Auth;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseController {

	protected ResponseBuilder responseBuilder; 
	protected String file;
	protected HashMap<Long, String> values;
	protected Connection connection;
	protected String multOrgCd;
	protected String hashCode;
	protected Auth auth;
	protected String dtHrRecebimento;
	
	public ResponseController withResponseBuilder(ResponseBuilder responseBuilder) {
		this.responseBuilder = responseBuilder;
		return this;
	}
	
	public ResponseController withFile(String file) {
		this.file = file;
		return this;
	}
	
	public ResponseController withValues(HashMap<Long, String> values) {
		this.values = values;
		return this;
	}
	
	public ResponseController withConnection(Connection connection) {
		this.connection = connection;
		return this;
	}
	
	public ResponseController withMultOrgCd(String multOrgCd) {
		this.multOrgCd = multOrgCd;
		return this;
	}
	
	public ResponseController withHashCode(String hashCode) {
		this.hashCode = hashCode;
		return this;
	}
	
	public ResponseController withAuth(Auth auth) {
		this.auth = auth;
		return this;
	}
	
	public ResponseController withDtHrRecebimento(String dtHrRecebimento) {
		this.dtHrRecebimento = dtHrRecebimento;
		return this;
	}
	
}
