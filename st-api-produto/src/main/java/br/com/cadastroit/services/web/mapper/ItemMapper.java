package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Item;
import br.com.cadastroit.services.web.dto.ItemDTO;
import br.com.cadastroit.services.web.dto.ItemResumeDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ItemMapper {

	public abstract Item toEntity(ItemDTO entity);

	public abstract ItemDTO toDto(Item entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "aliqIcms", source = "aliqIcms"),
		@Mapping(target = "codBarra", source = "codBarra"),
		@Mapping(target = "codItem", source = "codItem"),
		@Mapping(target = "descrItem", source = "descrItem"),
		@Mapping(target = "codAntItem", source = "codAntItem"),
		@Mapping(target = "empresaId", source = "empresa.id"),
		@Mapping(target = "empresa.pessoa", ignore = true)
	})
	public abstract ItemResumeDTO toResumeDTO(Item entity);
	
	public abstract List<ItemResumeDTO> toResumeDTO(List<Item> entity);
}
