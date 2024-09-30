package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;

import br.com.cadastroit.services.api.domain.DesifPlanoConta;
import br.com.cadastroit.services.web.dto.DesifPlanoContaDto;


@Mapper(componentModel = "spring")
public interface DesifPlanoContaMapper {

	DesifPlanoConta toEntity(DesifPlanoContaDto entityDto);

	DesifPlanoContaDto toDto(DesifPlanoConta entity);
}
