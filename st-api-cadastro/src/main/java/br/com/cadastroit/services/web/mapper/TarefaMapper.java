package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import br.com.cadastroit.services.web.dto.TarefaDetalhesDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TarefaMapper.class)
public abstract class TarefaMapper {

	public abstract Tarefa toEntity(TarefaDTO dto);

	public abstract TarefaDTO toDto(Tarefa entity);

	@Mappings({ @Mapping(target = "totalTarefas", source = "totalTarefas"), @Mapping(target = "totalPessoas", source = "totalPessoas"),
			@Mapping(target = "totalDepartamentos", source = "totalDepartamentos"), })
	public abstract TarefaDetalhesDTO toTarefaDTO(Tarefa entity);

	public abstract List<TarefaDetalhesDTO> toTarefaDTO(List<Tarefa> entity);

}
