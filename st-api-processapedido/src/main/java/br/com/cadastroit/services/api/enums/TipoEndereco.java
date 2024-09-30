package br.com.cadastroit.services.api.enums;

import java.util.Arrays;
import java.util.List;

public enum TipoEndereco {

	RESIDENCIAL(0, "Residencia"),
	COMERCIAL(1, "Comercial"),
	CORRESPONDENCIA(2, "Correspondencia"),
	RECADO(3, "Recado"),
	FERIAS(4, "Ferias");

	private int tipo;
	private String descricao;

	private TipoEndereco(int tipo, String descricao) {

		this.tipo = tipo;
		this.descricao = descricao;
	}

	public int getTipo() {

		return tipo;
	}

	public String getDescricao() {

		return descricao;
	}

	public static List<TipoEndereco> getOpcoesCadastroEndereco() {

		return Arrays.asList(new TipoEndereco[] { RESIDENCIAL, COMERCIAL, CORRESPONDENCIA, RECADO, FERIAS });
	}

}
