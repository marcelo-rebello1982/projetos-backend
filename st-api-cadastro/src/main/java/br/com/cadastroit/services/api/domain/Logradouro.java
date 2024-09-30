package br.com.cadastroit.services.api.domain;

import org.springframework.util.Assert;

import br.com.cadastroit.services.exceptions.GenericException;

public enum Logradouro {

	OUTROS("Outros"),
	AEROPORTO("Aeroporto"),
	ALAMEDA("Alameda"),
	AREA("Área"),
	AVENIDA("Avenida"),
	CAMPO("Campo"),
	CHACARA("Chácara"),
	COLONIA("Colônia"),
	CONDOMINIO("Condomínio"),
	CONJUNTO("Conjunto"),
	DISTRITO("Distrito"),
	ESPLANADA("Esplanada"),
	ESTACAO("Estação"),
	ESTRADA("Estrada"),
	FAVELA("Favela"),
	FAZENDA("Fazenda"),
	FEIRA("Feira"),
	JARDIM("Jardim"),
	LADEIRA("Ladeira"),
	LAGO("Lago"),
	LAGOA("Lagoa"),
	LARGO("Largo"),
	LOTEAMENTO("Loteamento"),
	MORRO("Morro"),
	NUCLEO("Núcleo"),
	PARQUE("Parque"),
	PASSARELA("Passarela"),
	PATIO("Pátio"),
	PRACA("Praça"),
	QUADRA("Quadra"),
	RECANTO("Recanto"),
	RESIDENCIAL("Residencial"),
	RODOVIA("Rodovia"),
	RUA("Rua"),
	SETOR("Setor"),
	SITIO("Sítio"),
	TRAVESSA("Travessa"),
	TRECHO("Trecho"),
	TREVO("Trevo"),
	VALE("Vale"),
	VEREDA("Vereda"),
	VIA("Via"),
	VIADUTO("Viaduto"),
	VIELA("Viela"),
	VILA("Vila");

	Logradouro(String descricao) {

		this.descricao = descricao;
	}

	private String descricao;

	public String getDescricao() {

		return descricao;
	}

	public static Logradouro fromString(String descricao) {

		Assert.notNull(descricao, "logradouro nulo");

		for (Logradouro logradouro : Logradouro.values()) {
			if (descricao.equalsIgnoreCase(logradouro.getDescricao()))
				return logradouro;
		}

		throw new GenericException("Logradouro não encontrado");
	}

}
