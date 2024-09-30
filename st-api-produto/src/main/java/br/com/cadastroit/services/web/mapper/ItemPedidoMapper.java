package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.ItemPedido;
import br.com.cadastroit.services.web.dto.ItemPedidoDTO;
import br.com.cadastroit.services.web.dto.ItemPedidoResumeDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = ItemPedidoMapper.class)
public abstract class ItemPedidoMapper {
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "idItemped", source = "entity.item.id"),
		@Mapping(target = "aliqIcms", source = "entity.item.aliqIcms"),
		@Mapping(target = "codBarra", source = "entity.item.codBarra"),
		@Mapping(target = "descrItem", source = "entity.item.descrItem"),
		@Mapping(target = "quantidade", source = "entity.item.quantidade"),
	})
	public abstract ItemPedidoResumeDTO toResumeDTO(ItemPedido entity);
	
	public abstract List<ItemPedidoResumeDTO> toResumeDTO(List<ItemPedido> entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nroItemped", source = "nroItemped"),
		@Mapping(target = "qtdItemped", source = "qtdItemped"),
		@Mapping(target = "vlrDesconto", source = "vlrDesconto"),
		@Mapping(target = "pedido", source = "pedido"),
		@Mapping(target = "empresaId", source = "item.empresa.id"),
	})
	public abstract ItemPedidoDTO toDTO(ItemPedido entity);
	
	public abstract List<ItemPedidoDTO> toDTO(List<ItemPedido> dto);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nroItemped", source = "nroItemped"),
		@Mapping(target = "qtdItemped", source = "qtdItemped"),
		@Mapping(target = "vlrDesconto", source = "vlrDesconto"),
		@Mapping(target = "pedido", source = "pedido"),
		@Mapping(target = "item", source = "item"),
	})
	public abstract ItemPedido toEntity(ItemPedidoDTO entity);

	public abstract List<ItemPedido> toEntity(List<ItemPedidoDTO> dto);

}
