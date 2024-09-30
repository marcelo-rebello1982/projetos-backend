package br.com.cadastroit.services.api.enums;

import java.util.Arrays;
import java.util.List;

public enum BalancoEstoqueStatus {

	PENDENTE("Pendente"),
	EM_PROCESSAMENTO("Em processamento"),
	PROCESSADO("Processado"),
	EM_EXCLUSAO("Em exclusão"),
	ERRO_PROCESSAMENTO("Erro de processamento"),
	ERRO_EXCLUSAO("Erro de exclusão"),
	ALTERACAO_PENDENTE("Alteração pendente");

	private String descricao;

	BalancoEstoqueStatus(String descricao) {

		this.descricao = descricao;
	}

	public String getDescricao() {

		return descricao;
	}

	public static List<BalancoEstoqueStatus> obterParaPesquisa() {

		return Arrays.asList(PENDENTE, EM_PROCESSAMENTO, PROCESSADO, ERRO_PROCESSAMENTO, EM_EXCLUSAO, ALTERACAO_PENDENTE);
	}

}
