package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Pensamento;
import br.com.cadastroit.services.web.dto.PensamentoDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PensamentoMapper.class)
public abstract class  PensamentoMapper {

	public abstract Pensamento toEntity(PensamentoDTO dto);
	
	public abstract PensamentoDTO toDTO(Pensamento dto);
	
	public abstract List<PensamentoDTO> toDTO(List<Pensamento> entity);

	
	//@Mappings({
	//	@Mapping(target = "id", source = "id"),
	//	@Mapping(target = "nome", source = "nome"),
	//	@Mapping(target = "email", source = "email"),
	//	@Mapping(target = "fone", source = "fone"),
	//	@Mapping(target = "dataNascimento", source = "dataNascimento"),
	//	@Mapping(target = "numeroTelefone", source = "fone"),
	//	@Mapping(target = "departamento", source = "departamento"),
	//	@Mapping(target = "createAt", source = "createAt"),
	//	@Mapping(target = "updateAt", source = "updateAt"),
	//	@Mapping(target = "tipoDocumento", source = "tipoDocumento"),
	//	@Mapping(target = "tipoPessoa", source = "tipoPessoa"),
	//	@Mapping(target = "status", source = "status"),
	// })
	// public abstract PessoaDTO toDto(Pessoa entity);
	

	
}


