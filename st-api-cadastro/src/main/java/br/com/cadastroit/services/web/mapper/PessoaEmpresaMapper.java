package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.cadastroit.services.api.domain.PessoaEmpresa;
import br.com.cadastroit.services.web.dto.PessoaEmpresaDTO;
import br.com.cadastroit.services.web.dto.PessoaEmpresaResumedDTO;

@Mapper(componentModel = "spring")
public interface PessoaEmpresaMapper {

	PessoaEmpresa toEntity(PessoaEmpresaDTO dto);

	PessoaEmpresaDTO toDto(PessoaEmpresa entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nome", source = "nome"),
		@Mapping(target = "juridica", source = "juridica"),
		@Mapping(target = "fisica", source = "fisica"),
		@Mapping(target = "empresa", source = "empresa"),
	})
	public abstract PessoaEmpresaResumedDTO toPessoaEmpresaResume(PessoaEmpresa entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "fone", source = "fone"),
		@Mapping(target = "email", source = "email"),
		@Mapping(target = "juridica", source = "juridica"),
		@Mapping(target = "fisica", source = "fisica"),
		@Mapping(target = "empresa", source = "empresa"),
	})
	public abstract List<PessoaEmpresaDTO> toDto(List<PessoaEmpresa> entity);
}
