package br.com.cadastroit.services.web.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.web.dto.PedidoCompleteDTO;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.web.dto.PedidoResumeDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PedidoMapper {

	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nroPedido", source = "nroPedido"),
		@Mapping(target = "qtdTotal", source = "qtdTotal"),
		@Mapping(target = "vlrTotal", source = "vlrTotal"),
		@Mapping(target = "dataCompra", source = "dataCompra"),
		@Mapping(target = "status", source = "status"),
	})
	public abstract PedidoResumeDTO toResumeDTO(Pedido entity);
	
	@Mappings({
		@Mapping(target = "id", source = "id"),
		@Mapping(target = "nroPedido", source = "nroPedido"),
		@Mapping(target = "qtdTotal", source = "qtdTotal"),
		@Mapping(target = "vlrTotal", source = "vlrTotal"),
		@Mapping(target = "dataCompra", source = "dataCompra" , dateFormat = "yyyyMMDD"),
		@Mapping(target = "status", source = "status"),
	})
	public abstract PedidoCompleteDTO toCompleteDTO(Pedido entity);
	
	public abstract List<PedidoResumeDTO> toResumeDTO(List<Pedido> entity);
	
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
	
	public abstract List<Pedido> toEntity(List<PedidoDTO> entity);

	
	@AfterMapping
	public void afterMapToPesquisa(Pedido entity, @MappingTarget PedidoResumeDTO dto) {
		
	}
	
}
