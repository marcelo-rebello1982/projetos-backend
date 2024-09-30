package br.com.cadastroit.services.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.web.dto.PessoaDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PessoaMapper.class)
public abstract class  PessoaMapper {

	public abstract Pessoa toEntity(PessoaDTO dto);
	
	public abstract PessoaDTO toDto(Pessoa entity);
	
}


