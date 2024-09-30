package br.com.cadastroit.services.api.enums;

public enum TipoTelefone {

	CEL("Celular"),
	RES("Residencial"),
	COM("Comercial"),
	WTS("Whatsapp"),
	REC("Recado");

	private String descricao;

	private TipoTelefone(String descricao) {

		this.descricao = descricao;
	}

	public String getDescricao() {

		return this.descricao;
	}

}
