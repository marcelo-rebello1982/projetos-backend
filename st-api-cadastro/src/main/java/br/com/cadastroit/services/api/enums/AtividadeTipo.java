package br.com.cadastroit.services.api.enums;

import java.util.Arrays;
import java.util.List;

public enum AtividadeTipo {

	INDUSTRIA(1l, "Indústria"),
	IMPORTACAO(10L, "Importação"),
	COMERCIO(5l, "Comércio"),
	SERVICO_ISS(8l, "Serviço ISS"),
	SERVICO_ICMS(9l, "Serviço ICMS");

	private Long codigo;
	private String descricao;

	AtividadeTipo(Long codigo, String descricao) {

		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Long getCodigo() {

		return codigo;
	}

	public String getDescricao() {

		return descricao;
	}

	public static List<AtividadeTipo> getOpcoesCadastro() {

		return Arrays.asList(INDUSTRIA, COMERCIO, SERVICO_ISS, SERVICO_ICMS);
	}

}
