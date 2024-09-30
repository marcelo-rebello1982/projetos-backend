package br.com.cadastroit.services.api.enums;
public enum MetodoControle {

	ESTOCAVEL("Estocável"),
	LOTE("Lote"),
	SERIE("Série"),
	NAO_ESTOCAVEL("Não estocável");
	
	private String descricao;

	MetodoControle(String descricao) {

		this.descricao = descricao;
	}

	public String getDescricao() {

		return descricao;
	}

}