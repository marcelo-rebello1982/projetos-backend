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
public class ItemPedidoResumeDTO  {

	private Long id;

	private Long idItemped;
	
	private BigDecimal aliqIcms;

	private BigDecimal qtdItemped;

	private BigDecimal vlrDesconto;
	
	private String codBarra;
	
	private String codItem;
	
	private String descrItem;
	
	private BigDecimal quantidade;
	
	private Boolean existeItensPedido;

	private ItemResumeDTO item;
	
}
