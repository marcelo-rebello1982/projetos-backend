package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.ConstrNfs;
import br.com.cadastroit.services.web.dto.ConstrNfsDto;

@Mapper(componentModel = "spring")
public interface ConstrNfsMapper {

	ConstrNfs toEntity(ConstrNfsDto entityDto);

	ConstrNfsDto toDto(ConstrNfs entity);
}
