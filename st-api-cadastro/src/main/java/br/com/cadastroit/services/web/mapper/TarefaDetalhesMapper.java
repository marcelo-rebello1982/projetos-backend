package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import br.com.cadastroit.services.web.dto.TarefaDetalhesDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TarefaDetalhesMapper {
	
	 public abstract TarefaDTO toDto(Tarefa entity);

	 public abstract List<TarefaDTO> toDto(List<Tarefa> entity);

	@Mappings({ @Mapping(target = "totalPessoas", source = "entity.totalPessoas"),
		@Mapping(target = "totalTarefas", source = "entity.totalTarefas"),
		@Mapping(target = "tarefas", source = "entity"),
		@Mapping(target = "totalDepartamentos", source = "entity.totalDepartamentos") })
	public abstract List<TarefaDetalhesDTO> toDetalhesDto(List<Tarefa> entity);
	
	@Mappings({ @Mapping(target = "totalPessoas", source = "entity.totalPessoas"),
		@Mapping(target = "totalTarefas", source = "entity.totalTarefas"),
		@Mapping(target = "totalDepartamentos", source = "entity.totalDepartamentos") })
	public abstract TarefaDetalhesDTO toDetalhesDto(Tarefa entity);
}