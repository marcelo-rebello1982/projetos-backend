package br.com.cadastroit.services.api.enums;

public enum SolicitacaoDocumentoStatus {

	EM_AGUARDO("Em aguardo"),
	ENVIADO("Enviado"),
	APROVADO("Aprovado"),
	APROVADO_PARCIALMENTE("Aprovado parcialmente"),
	REPROVADO("Reprovado"),;

	private String descricao;

	private SolicitacaoDocumentoStatus(String descricao) {

		this.descricao = descricao;
	}

	public String getDescricao() {

		return descricao;
	}

}
