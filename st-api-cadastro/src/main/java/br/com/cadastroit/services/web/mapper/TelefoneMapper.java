package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Telefone;
import br.com.cadastroit.services.web.dto.TelefoneDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TelefoneMapper.class)
public abstract class TelefoneMapper {

	public abstract Telefone toEntity(TelefoneDTO dto);

	@Mappings({
		@Mapping(target = "pessoaId", source = "pessoa.id"),
	})
	public abstract TelefoneDTO toDto(Telefone entity);
	
	public abstract List<Telefone> toList(List<TelefoneDTO> entity);
	
	public abstract List<TelefoneDTO> toDto(List<Telefone> entity);


}
