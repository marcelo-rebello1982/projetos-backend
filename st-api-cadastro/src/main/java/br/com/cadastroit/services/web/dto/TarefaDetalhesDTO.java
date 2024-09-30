package br.com.cadastroit.services.web.dto;

import java.util.List;

import br.com.cadastroit.services.api.domain.Tarefa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TarefaDetalhesDTO  {
	
private Long totalTarefas;
	
	private Long totalPessoas;
	
	private Long totalDepartamentos;
	
	private boolean todasFinalizadas;
	
	private boolean semTarefasCadastradas;
	
	private List<TarefaDTO> tarefas;

	public TarefaDetalhesDTO(Tarefa tarefa) {

	}
}
