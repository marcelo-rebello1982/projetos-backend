package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.InterNfs;
import br.com.cadastroit.services.web.dto.InterNfsDto;

@Mapper(componentModel = "spring")
public interface InterNfsMapper {

	InterNfs toEntity(InterNfsDto entityDto);

	InterNfsDto toDto(InterNfs entity);
}
