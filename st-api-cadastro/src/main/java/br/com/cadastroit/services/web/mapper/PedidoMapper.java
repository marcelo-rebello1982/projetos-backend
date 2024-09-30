package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.web.dto.PedidoDTO;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nroPedido", source = "nroPedido"),
		@Mapping(target = "qtdTotal", source = "qtdTotal"),
		@Mapping(target = "vlrTotal", source = "vlrTotal"),
		@Mapping(target = "dataCompra", source = "dataCompra"),
		@Mapping(target = "status", source = "status"),
		@Mapping(target = "pessoa", source = "pessoa"),
	})
	public abstract PedidoDTO toDTO(Pedido entity);
	
	public abstract List<PedidoDTO> toDTO(List<Pedido> entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nroPedido", source = "nroPedido"),
		@Mapping(target = "qtdTotal", source = "qtdTotal"),
		@Mapping(target = "vlrTotal", source = "vlrTotal"),
		@Mapping(target = "dataCompra", source = "dataCompra"),
		@Mapping(target = "status", source = "status"),
		@Mapping(target = "pessoa", source = "pessoa"),
	})
	public abstract Pedido toEntity(PedidoDTO entity);
	
	public abstract List<Pedido> toEntity(List<PedidoDTO> dto);

}
