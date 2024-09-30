package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import br.com.cadastroit.services.api.domain.NfServ;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class StatusDmStProcDto {

	private String multOrgCd;
	private String dtEmissao;
	public Long qtdProcess;
	public Long qtdPendencia;
	public Long qtdAutorizada;
	public Long qtdCancelada;
	
	private BigDecimal dmStProc;
	private Date dtEmissIni;
	private Date dtEmissFim;
	private String processamento;
	private String descr;
	private List<NfServ> nfServ;

}
