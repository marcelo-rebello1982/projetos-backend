package br.com.cadastroit.services.commons.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalhamentoTotalPorCidade {
	
	private String uf;
	private String descr;
	private String codIbgeCidade;
	private String dtEmissao;
	private Integer dmDbDestino;
	private Long qtdTotalNotasProcessadas;
	private Long qtdTotalNotasPendencia;
	private Long qtdTotalNotasAutorizadas;
	private Long qtdTotalNotasCanceladas;
	private Long qtdTotalNotasEmitidas;
	
}
