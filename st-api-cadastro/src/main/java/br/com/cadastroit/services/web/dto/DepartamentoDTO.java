package br.com.cadastroit.services.web.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DepartamentoDTO  {

	private Long id;
	
	private String descr;
	
	public Long quantidadePessoas;
	
	public Long quantidadeTarefas;
	
    private List<PessoaEnderecoTelefoneResumedDTO> pessoas;
    
    private int page;

	private int lenght;
	
	private Map<String, String> requestParams;
	
}
