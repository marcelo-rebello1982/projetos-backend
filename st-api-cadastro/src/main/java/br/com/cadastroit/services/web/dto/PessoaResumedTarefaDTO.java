package br.com.cadastroit.services.web.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResumedTarefaDTO {
	
	private Long id;
	
	private String nome;

	private String email;

	private String fone;
	
	private List<TarefaDTO> tarefas;
	
}