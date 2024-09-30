package br.com.cadastroit.services.api.enums;

public enum TipoCadastro {

	CLIENTE("Cliente"),
	FORNECEDOR("Fornecedor"),
	PRESTADOR_SERVICO("Prestador de serviço"),
	COOPERADO("Cooperado"),
	FUNCIONARIO("Funcionário"),
	SOCIO_PROPRIETARIO("Sócio proprietário"),
	ACIONISTA("Acionista"),
	OUTROS("Outros"),
	TECNICO("Técnico"),
	EMPRESA("Empresa");

	private String descricao;

	private TipoCadastro(String descricao) {

		this.descricao = descricao;
	}

	public String getDescricao() {

		return this.descricao;
	}
}
