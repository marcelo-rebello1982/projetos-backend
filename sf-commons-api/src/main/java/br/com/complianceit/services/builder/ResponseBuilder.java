package br.com.complianceit.services.builder;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
@Getter
public class ResponseBuilder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Long protocolo;
	protected String dtHrRecebimento;
	protected Long situacao;
	protected String message;
	protected String token;
	
	public ResponseBuilder withProtocolo(Long protocolo) {
		this.protocolo = protocolo;
		return this;
	}
	
	public ResponseBuilder withDtHrRecebimento(String dtHrRecebimento) {
		this.dtHrRecebimento = dtHrRecebimento;
		return this;
	}
	
	public ResponseBuilder withSituacao(Long situacao) {
		this.situacao = situacao;
		return this;
	}
	
	public ResponseBuilder withMessage(String message) {
		this.message = message;
		return this;
	}
	
	public ResponseBuilder withToken(String token) {
		this.token = token;
		return this;
	}
}
