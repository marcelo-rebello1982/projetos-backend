package br.com.cadastroit.services.api.enums;

import br.com.cadastroit.services.exceptions.BusinessException;

public enum Status {

	INATIVO(0, "Inativo"),
	ATIVO(1, "Ativo"),
	EXCLUIDO(2, "Excluído");

	private int status;
	private String descricao;

	private Status(int status, String descricao) {

		this.status = status;
		this.descricao = descricao;
	}

	public int getStatus() {

		return status;
	}

	public String getDescricao() {

		return descricao;
	}

	public static Status getByStatus(int cod) {

		for (Status status : Status.values()) {
			if (cod == status.getStatus())
				return status;
		}

		throw new BusinessException("Status não encontrado.");
	}

}