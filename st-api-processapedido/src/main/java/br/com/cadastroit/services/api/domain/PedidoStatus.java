package br.com.cadastroit.services.api.domain;

public enum PedidoStatus {

	ATENDIDO("Atendido"),
	PARCIALMENTE_ATENDIDO("Parcialmente atendido"),
	CANCELADO("Cancelado"),
	EM_ANDAMENTO("Em andamento"),
	ABERTO("Aberto"),
	PENDENTE_APROVACAO("Pendente de aprovação"),
	PARCIALMENTE_APROVADO("Parcialmente aprovado"),
	APROVADO("Aprovado"),
	ENCERRADO("Encerrado"),;

	private String descricao;

	PedidoStatus(String descricao) {

		this.descricao = descricao;
	}

	public String getDescricao() {

		return descricao;
	}

}
