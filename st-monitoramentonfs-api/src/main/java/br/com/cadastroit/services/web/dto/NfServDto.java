package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.cadastroit.services.api.enums.StatusProcessamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfServDto {

	private Long id;
	
	@NotNull(message = "Campo multorgcd sem preenchimento")
	@Size(min = 1, max = 10, message = "Campo multorgCd nao pode ultrapassar o total de 10 caracteres.")
	private String multorgCd;

	@NotNull(message = "Campo cnpjemit sem preenchimento")
	@Size(min = 1, max = 14, message = "Campo cnpjEmit nao pode ultrapassar o total de 14 caracteres.")
	private String cnpjEmit;

	@NotNull(message = "Campo imemit sem preenchimento")
	@Size(min = 1, max = 15, message = "Campo imemit nao pode ultrapassar o total de 15 caracteres.")
	private String imEmit;

	@Size(min = 1, max = 3, message = "Campo serie nao pode ultrapassar o total de 3 caracteres.")
	private String serie;

	@NotNull(message = "Campo nroNf sem preenchimento")
	private Integer nroNf;

	// usado apenas nos filtros;
	private Date dtEmissIni;
	private Date dtEmissFim;

	@NotNull(message = "Campo dtEmiss sem preenchimento")
	private Date dtEmiss;

	private String hrEmiss;

	private Timestamp dtExeServ;

	@Size(min = 1, max = 60, message = "Campo chaveNfse nao pode ultrapassar o total de 60 caracteres.")
	private String chaveNfse;

	@NotNull(message = "Campo dmNatOper sem preenchimento")
	private BigDecimal dmNatOper;

	private Integer responsavelRetencao;

	private Integer nroRpsSubst;

	@Size(min = 1, max = 3, message = "Campo serieRpsSubst nao pode ultrapassar o total de 3 caracteres.")
	private String serieRpsSubst;

	@Size(min = 1, max = 2, message = "Campo cdCidadeModFiscal nao pode ultrapassar o total de 2 caracteres.")
	private String cdCidadeModFiscal;

	@Size(min = 1, max = 21, message = "Campo nroProc nao pode ultrapassar o total de 21 caracteres.")
	private String nroProc;

	@Size(min = 1, max = 10, message = "Campo sistOrig nao pode ultrapassar o total de 10 caracteres.")
	private String sistOrig;

	@Size(min = 1, max = 20, message = "Campo unidOrg nao pode ultrapassar o total de 20 caracteres.")
	private String unidOrg;

	private BigDecimal idNfErp;

	@NotNull(message = "Campo dmStProc sem preenchimento")
	private BigDecimal dmStProc;
	
	//usado apenas nos filtros
	private BigDecimal[] dmStProcValues;
	
	private Set<StatusProcessamento> statusProc;

	@NotNull(message = "Campo dtEntSist sem preenchimento")
	private Timestamp dtEntSist;

	@Size(min = 1, max = 60, message = "Campo nroAutNfs nao pode ultrapassar o total de 60 caracteres.")
	private String nroAutNfs;

	private Timestamp dtAutNfs;

	private String codVerifNfs;

	@Size(min = 1, max = 1000, message = "Campo link nao pode ultrapassar o total de 1000 caracteres.")
	private String link;

	@NotNull(message = "Campo dmIndReplica sem preenchimento")
	private BigDecimal dmIndReplica;

	private Timestamp dtReplica;

	private EmpresaDto empresa;
	
	private Filters filtersDto;

}
