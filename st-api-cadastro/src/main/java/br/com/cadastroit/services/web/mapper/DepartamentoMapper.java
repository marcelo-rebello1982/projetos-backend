package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.web.dto.DepartamentoDTO;

@Mapper(componentModel = "spring")
public interface DepartamentoMapper {

	Departamento toEntity(DepartamentoDTO dto);

	DepartamentoDTO toDto(Departamento entity);
}
