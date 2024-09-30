package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.FaturaNfs;
import br.com.cadastroit.services.web.dto.FaturaNfsDto;

@Mapper(componentModel = "spring")
public interface FaturaNfsMapper {

	FaturaNfs toEntity(FaturaNfsDto entityDto);

	FaturaNfsDto toDto(FaturaNfs entity);
}
