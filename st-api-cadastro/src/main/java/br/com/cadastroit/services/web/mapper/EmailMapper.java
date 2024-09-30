package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.Email;
import br.com.cadastroit.services.web.dto.EmailDTO;

@Mapper(componentModel = "spring")
public interface EmailMapper {

	Email toEntity(EmailDTO dto);

	EmailDTO toDto(Email entity);
}
