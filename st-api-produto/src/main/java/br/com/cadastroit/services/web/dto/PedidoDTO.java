package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Calendar;

import br.com.cadastroit.services.api.domain.PedidoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO  {

	private Long id;

	private String nroPedido;

	private BigDecimal qtdTotal;

	private BigDecimal vlrTotal;

	@Builder.Default
    private Boolean deleted = Boolean.FALSE;
	
	@Builder.Default
    private Boolean aprovado = Boolean.FALSE;

	private Calendar dataCompra;
	
	@Builder.Default
	private Calendar dtUpdate = Calendar.getInstance();

	@Builder.Default
	private PedidoStatus status = PedidoStatus.EM_ANDAMENTO;
	
	private PessoaDTO pessoa;
	
}
