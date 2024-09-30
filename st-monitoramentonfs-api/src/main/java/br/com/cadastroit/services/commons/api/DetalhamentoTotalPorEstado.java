package br.com.cadastroit.services.commons.api;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalhamentoTotalPorEstado {

	private String uf;
	private String multOrgCd;
	private Integer qtdTotalCidades;
	private Long qtdTotalNotasProcessadas;
	private Long qtdTotalNotasPendencia;
	private Long qtdTotalNotasAutorizadas;
	private Long qtdTotalNotasCanceladas;
	private Long qtdTotalNotasEmitidas;
	private DetalhamentoTotalPorCidade detalhamentoTotalPorCidade;
	private List<DetalhamentoTotalPorCidade> listaDetalhamentoTotal;
	private AtomicReference<List<DetalhamentoTotalPorCidade>> listDetalhTotalPorCidade;
	
}

