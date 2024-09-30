package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoPesquisaDTO  {

	private Long id;

	private Integer nroItemped;

	private BigDecimal qtdItemped;

	private BigDecimal vlrDesconto;

	private PedidoDTO pedido;
	
	private ItemDTO item;
}
