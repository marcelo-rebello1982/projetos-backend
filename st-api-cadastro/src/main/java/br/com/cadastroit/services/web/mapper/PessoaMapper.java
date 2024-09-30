package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.web.dto.PessoaDTO;
import br.com.cadastroit.services.web.dto.PessoaEnderecoTelefoneResumedDTO;
import br.com.cadastroit.services.web.dto.PessoaResumedDTO;
import br.com.cadastroit.services.web.dto.PessoaResumedTarefaDTO;
import br.com.cadastroit.services.web.dto.PessoaTelefoneResumedDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = PessoaMapper.class)
public abstract class  PessoaMapper {

	public abstract Pessoa toEntity(PessoaDTO dto);

	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nome", source = "nome"),
		@Mapping(target = "tarefas", source = "tarefas"),
	})
	public abstract List<PessoaDTO> toDto(List<Pessoa> entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nome", source = "nome"),
		@Mapping(target = "tarefas", source = "tarefas"),
	})
	public abstract PessoaResumedTarefaDTO toResumedTarefaDto( Pessoa entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nome", source = "nome"),
		@Mapping(target = "telefone", source = "telefone"),
		@Mapping(target = "enderecos", source = "enderecos"),
	})
	public abstract PessoaEnderecoTelefoneResumedDTO toResumedDto ( Pessoa entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nome", source = "nome"),
		@Mapping(target = "telefone", source = "telefone"),
		@Mapping(target = "enderecos", source = "enderecos"),
	})
	public abstract PessoaTelefoneResumedDTO toPessoaDto(Pessoa entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nome", source = "nome"),
		@Mapping(target = "email", source = "email"),
		@Mapping(target = "fone", source = "fone"),
		@Mapping(target = "dataNascimento", source = "dataNascimento"),
		@Mapping(target = "numeroTelefone", source = "fone"),
		@Mapping(target = "departamento", source = "departamento"),
		@Mapping(target = "createAt", source = "createAt"),
		@Mapping(target = "updateAt", source = "updateAt"),
		@Mapping(target = "tipoDocumento", source = "tipoDocumento"),
		@Mapping(target = "tipoPessoa", source = "tipoPessoa"),
		@Mapping(target = "status", source = "status"),
	})
	public abstract PessoaDTO toDto(Pessoa entity);
	
	public abstract List<PessoaResumedDTO> toPessoaResumedDto(List<Pessoa> entity);

	public abstract List<PessoaTelefoneResumedDTO> toPessoaTelefoneDto(List<Pessoa> entity);
	
}


