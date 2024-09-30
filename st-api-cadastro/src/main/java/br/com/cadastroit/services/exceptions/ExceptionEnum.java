package br.com.cadastroit.services.exceptions;
public enum ExceptionEnum {

	NOT_AUTHENTICATED_EXCEPTION("exception.not.authenticated"),

	ILLEGAL_PERSISTENT_EXCEPTION("exception.illegal.persistent.object"),

	NOT_FOUND_EXCEPTION("exception.default.not.found"),

	SOAP_CLIENT_EXCEPTION("exception.soapclient"),

	GENERIC_BUSINESS_EXCEPTION("exception.business.generic"),

	DOCUMENTO_REPETIDO_EXCEPTION("exception.documento.repetido"),

	CLASSIFICACAO_FISCAL_NAO_CONFIGURADA("exception.classificacao.fiscal.nao.configurada"),

	REST_CLIENT_EXCEPTION("exception.restclient");

	private String messageCode;

	ExceptionEnum(String messageCode) {

		this.messageCode = messageCode;
	}

	public String getMessageCode() {

		return this.messageCode;
	}

}
