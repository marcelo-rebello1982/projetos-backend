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
public class PedidoInformacoesEnvioDTO {

	private Long id;

	private String nroPedido;

	private BigDecimal qtdTotal;

	private BigDecimal vlrTotal;

	private Calendar dataCompra;

	private PedidoStatus status;

	private PessoaDTO pessoa;

}
