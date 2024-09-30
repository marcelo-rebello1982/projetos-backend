package br.com.cadastroit.services.api.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Filters {

	private int status;

	private String descricao;

	private Date dataInicial;

	private Date dataFinal;
	
	private String protocolo;
	
	List<String> dataFilters;
}