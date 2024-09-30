package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.web.dto.EmpresaDto;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

	Empresa toEntity(EmpresaDto entityDto);

	EmpresaDto toDto(Empresa entity);
}
