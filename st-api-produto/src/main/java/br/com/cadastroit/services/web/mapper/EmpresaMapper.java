package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.web.dto.EmpresaDTO;

@Mapper(componentModel = "spring")
public interface EmpresaMapper {

	Empresa toEntity(EmpresaDTO entityDto);

	EmpresaDTO toDto(Empresa entity);
}
