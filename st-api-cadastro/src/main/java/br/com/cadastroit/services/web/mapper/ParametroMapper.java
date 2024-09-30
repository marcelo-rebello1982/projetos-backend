package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Parametro;
import br.com.cadastroit.services.api.domain.ParametroBoolean;
import br.com.cadastroit.services.api.domain.ParametroInteger;
import br.com.cadastroit.services.api.domain.ParametroNumber;
import br.com.cadastroit.services.api.domain.ParametroString;
import br.com.cadastroit.services.web.dto.ParametroDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ParametroMapper {
	
	public abstract ParametroDTO booleanToDto(ParametroBoolean entity);

	public abstract List<ParametroDTO> booleanToDto(List<ParametroBoolean> entities);

	public abstract ParametroDTO integerToDto(ParametroInteger entity);

	public abstract List<ParametroDTO> integerToDto(List<ParametroInteger> entities);

	public abstract ParametroDTO numberToDto(ParametroNumber entity);

	public abstract List<ParametroDTO> numberToDto(List<ParametroNumber> entities);

	public abstract ParametroDTO stringToDto(ParametroString entity);

	public abstract List<ParametroDTO> stringToDto(List<ParametroString> entities);
	
	public abstract List<ParametroDTO> toListDto(List<Parametro> entities);

	
}
