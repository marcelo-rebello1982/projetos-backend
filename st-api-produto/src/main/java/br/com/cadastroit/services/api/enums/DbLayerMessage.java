package br.com.cadastroit.services.api.enums;

public enum DbLayerMessage {

	NO_RESULT_POR_ID("Sem registros de %s para a busca ID%s[%s]:"),
	ERROR_DB_LAYER("Erro no modulo de %s para %s, [error] = %s"),
	EMPTY_MSG("List is empty...");

	private String message;

	DbLayerMessage(String message) {

		this.message = message;
	}

	public String message() {

		return this.message;
	}
}
