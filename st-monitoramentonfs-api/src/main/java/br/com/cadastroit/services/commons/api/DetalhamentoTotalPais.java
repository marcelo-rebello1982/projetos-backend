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
public class DetalhamentoTotalPais {
	
	private Integer qtdTotalEstados;
	private Long qtdTotalNotasProcessadas;
	private Long qtdTotalNotasPendencia;
	private Long qtdTotalNotasAutorizadas;
	private Long qtdTotalNotasCanceladas;
	private Long qtdTotalNotasEmitidas;
	private AtomicReference<List<DetalhamentoTotalPorEstado>> listDetalhTotalPorEstado;
}

