package br.com.cadastroit.services.commons.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalhamentoTotalEstadoCidade {
	
	private Integer qtdTotalCidades;
	private Long qtdTotalNotasProcessadas;
	private Long qtdTotalNotasPendencia;
	private Long qtdTotalNotasAutorizadas;
	private Long qtdTotalNotasCanceladas;

	private Long qtdTotalNotasEmitidas;
	private List<DetalhamentoTotalPorCidade> listDetalhamentoTotalCidades;
	
}
