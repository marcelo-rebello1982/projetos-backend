package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NF_SERV")
public class NfServ implements Serializable {

	private static final long serialVersionUID = -4284157293490962051L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NF_SERV_ID_SEQ")
	@SequenceGenerator(name="NF_SERV_GENERATOR", sequenceName = "NF_SERV_ID_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "MULTORG_CD", nullable = false)
	private String multorgCd;

	@Column(name = "CNPJ_EMIT", nullable = false)
	private String cnpjEmit;

	@Column(name = "IM_EMIT", nullable = false)
	private String imEmit;

	@Column(name = "SERIE")
	private String serie;

	@Column(name = "NRO_NF", nullable = false)
	private Integer nroNf;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_EMISS", nullable = false)
	private Date dtEmiss;

	@Column(name = "HR_EMISS")
	private String hrEmiss;

	@Column(name = "DT_EXE_SERV")
	private Timestamp dtExeServ;

	@Column(name = "CHAVE_NFSE")
	private String chaveNfse;

	@Column(name = "DM_NAT_OPER", nullable = false)
	private BigDecimal dmNatOper;

	@Column(name = "RESPONSAVEL_RETENCAO")
	private Integer responsavelRetencao;

	@Column(name = "NRO_RPS_SUBST")
	private Integer nroRpsSubst;

	@Column(name = "SERIE_RPS_SUBST")
	private String serieRpsSubst;

	@Column(name = "CD_CIDADE_MOD_FISCAL")
	private String cdCidadeModFiscal;

	@Column(name = "NRO_PROC")
	private String nroProc;

	@Column(name = "SIST_ORIG")
	private String sistOrig;

	@Column(name = "UNID_ORG")
	private String unidOrg;

	@Column(name = "ID_NF_ERP")
	private BigDecimal idNfErp;

	@Column(name = "DM_ST_PROC", nullable = false)
	private BigDecimal dmStProc;

	@Column(name = "DT_ENT_SIST", nullable = false)
	private Timestamp dtEntSist;

	@Column(name = "NRO_AUT_NFS")
	private String nroAutNfs;

	@Column(name = "DT_AUT_NFS")
	private Timestamp dtAutNfs;

	@Column(name = "CODVERIFNFS")
	private String codVerifNfs;

	@Column(name = "LINK")
	private String link;

	@Column(name = "DM_IND_REPLICA", nullable = false)
	private BigDecimal dmIndReplica;

	@Column(name = "DT_REPLICA")
	private Timestamp dtReplica;
	
	@ManyToOne
	@JoinColumn(name = "EMPRESA_ID")
	private Empresa empresa;
}
