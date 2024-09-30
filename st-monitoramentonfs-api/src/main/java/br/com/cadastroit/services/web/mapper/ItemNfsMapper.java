package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.ItemNfs;
import br.com.cadastroit.services.web.dto.ItemNfsDto;

@Mapper(componentModel = "spring")
public interface ItemNfsMapper {

	ItemNfs toEntity(ItemNfsDto entityDto);

	ItemNfsDto toDto(ItemNfs entity);
}
