package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Endereco;
import br.com.cadastroit.services.web.dto.EnderecoDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = EnderecoMapper.class)
public abstract class EnderecoMapper {
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "logradouro", source = "logradouro"),
		@Mapping(target = "numero", source = "numero"),
		@Mapping(target = "complemento", source = "complemento"),
		@Mapping(target = "bairro", source = "bairro"),
		@Mapping(target = "pessoaId", source = "pessoa.id"),
		@Mapping(target = "tipoEndereco", source = "tipoEndereco"),
	})
	public abstract EnderecoDTO toDto( Endereco endereco);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "logradouro", source = "logradouro"),
		@Mapping(target = "numero", source = "numero"),
		@Mapping(target = "complemento", source = "complemento"),
		@Mapping(target = "bairro", source = "bairro"),
		@Mapping(target = "tipoEndereco", source = "tipoEndereco"),
	})
	public abstract Endereco toEntity( EnderecoDTO endereco);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "logradouro", source = "logradouro"),
		@Mapping(target = "numero", source = "numero"),
		@Mapping(target = "complemento", source = "complemento"),
		@Mapping(target = "bairro", source = "bairro"),
		@Mapping(target = "pessoaId", source = "pessoa.id"),
		@Mapping(target = "tipoEndereco", source = "tipoEndereco"),
	})
	public abstract List<EnderecoDTO> toListDto( List<Endereco> endereco);
}
