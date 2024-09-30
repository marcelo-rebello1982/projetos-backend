package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.web.dto.NfServDto;

@Mapper(componentModel = "spring")
public interface NfServMapper {

	NfServ toEntity(NfServDto entityDto);

	NfServDto toDto(NfServ entity);
}
