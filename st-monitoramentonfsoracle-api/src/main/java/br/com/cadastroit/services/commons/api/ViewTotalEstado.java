package br.com.cadastroit.services.commons.api;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewTotalEstado {
	
	private String multOrgCd;
	private String dtEmissao;
	private String uf;
	private String descrCidade;
	private Long qtdTotalNotasProc;
	private Long qtdTotalNotasPend;
	private Long qtdTotalNotasAutoriz;
	private Long qtdTotalNotasCancel;
	
	private Date dtEmissIni;
	private Date dtEmissFim;
	private ViewCidadeEmpresa viewCidadeEmpresa;

}
