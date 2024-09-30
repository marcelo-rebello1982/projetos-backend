package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filters {

	private String finalValues;
	public  String multOrgCd;
	private String siglaEstado;
	private String descrCidade;
	private String dtEmissao;
	private String cnpjEmit;
	private String imEmit;
	private String serie;
	private String nroNf;
	private Integer[] nroNfValues;
	private String nroNfIni;
	private String nroNfFim;
	private String nroDocTomador;
	private String nomeTomador;
	private int contador;
	private Long qtdProcess;
	private Long qtdPendenc;
	private Long qtdAutoriz;
	private Long qtdCancel;
	private BigDecimal dmStProc;
	private Integer[] dmStProcValues;
	private Date dtEmissIni;
	private Date dtEmissFim;
	private String processamento;
	private String descr;
	private EmpresaDto empresa;
	private AtomicBoolean isDestalharPorCid;
	private AtomicBoolean isDestalharPorEstado;
	private AtomicBoolean isDestalharItemNfServ;
}

