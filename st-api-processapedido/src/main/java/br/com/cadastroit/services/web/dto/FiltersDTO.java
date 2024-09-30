package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltersDTO {

	private BigDecimal[] dmStProc;
	private String token;
	private String serie;
	private String nroPedido;
	private Boolean aprovado;
	private String dtHrEntSist;
	private String codMod;
	private int dmStEmail;
	private Long empresaId;
	private String[] idsConsolidar;
	private Date dtEmissIni;
	private Date dtEmissFim;
	private int status;
	private String descricao;
	private Date dataInicial;
	private Date dataFinal;
	private String protocolo;
	private List<String> dataFilters;
	private PessoaDTO pessoa;
	private transient Integer filters;
}
