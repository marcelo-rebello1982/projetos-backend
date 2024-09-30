package br.com.cadastroit.services.commons.api;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewTotalItemNfServ {

	private String nfServId;
	private String multOrgCd;
	private String uf;
	private String descrCidade;
	private String ibgeCidade;
	private Integer dmDbDestino;
	private String cnpjEmit;
	private String imEmit;
	private String serie;
	private Integer nroNf;
	private String dtEmissao;
	private String hrEmissao;
	private Timestamp dtExeServ;
	private BigDecimal dmStProc;
	private String nroDocTomador;
	private String nomeTomador;
	private Double vlTotServ;
	private Double vlTotDesconto;
	private Double vlTotRetido;
	private Double vlTotIss;
	private Double vlTotalNf;
	
}
