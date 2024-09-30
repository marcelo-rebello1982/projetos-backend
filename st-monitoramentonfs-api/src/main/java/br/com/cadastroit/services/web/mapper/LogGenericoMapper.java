package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.LogGenerico;
import br.com.cadastroit.services.web.dto.LogGenericoDto;

@Mapper(componentModel = "spring")
public interface LogGenericoMapper {

	LogGenerico toEntity(LogGenericoDto entityDto);

	LogGenericoDto toDto(LogGenerico entity);
}
