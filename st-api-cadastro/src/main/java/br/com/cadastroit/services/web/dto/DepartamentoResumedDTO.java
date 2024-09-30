package br.com.cadastroit.services.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;


@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DepartamentoResumedDTO  {

	private Long id;
	
	private String descr;
	
	public Long quantidadePessoas;
	
	public Long quantidadeTarefas;

	public DepartamentoResumedDTO(Long id, String descr) {

		super();
		this.id = id;
		this.descr = descr;
	}

	
	public Long getId() {
	
		return id;
	}

	
	public void setId(Long id) {
	
		this.id = id;
	}

	
	public String getDescr() {
	
		return descr;
	}

	
	public void setDescr(String descr) {
	
		this.descr = descr;
	}
	
	
	
}
