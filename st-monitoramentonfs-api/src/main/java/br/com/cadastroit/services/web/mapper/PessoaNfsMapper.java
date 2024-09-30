package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.PessoaNfs;
import br.com.cadastroit.services.web.dto.PessoaNfsDto;

@Mapper(componentModel = "spring")
public interface PessoaNfsMapper {

	PessoaNfs toEntity(PessoaNfsDto entityDto);

	PessoaNfsDto toDto(PessoaNfs entity);
}
