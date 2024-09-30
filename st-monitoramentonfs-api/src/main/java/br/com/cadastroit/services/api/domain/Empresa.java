package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "EMPRESA")
@SequenceGenerator(name = "EMPRESA_SEQ", sequenceName = "EMPRESA_SEQ", allocationSize = 1, initialValue = 1)
public class Empresa implements Serializable {

	private static final long serialVersionUID = -3339752350095644928L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "EMPRESA_SEQ")
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "MULTORG_CD", nullable = false)
	private String multorgCd;

	@Column(name = "CNPJ", nullable = false)
	private String cnpj;

	@Column(name = "IM", nullable = false)
	private String im;

	@Column(name = "NOME", nullable = false)
	private String nome;

	@Column(name = "FANTASIA")
	private String fantasia;

	@Column(name = "LOGRAD", nullable = false)
	private String lograd;

	@Column(name = "NRO", nullable = false)
	private String nro;

	@Column(name = "COMPL")
	private String compl;

	@Column(name = "BAIRRO", nullable = false)
	private String bairro;

	@Column(name = "IBGE_CIDADE", nullable = false)
	private String ibgeCidade;

	@Column(name = "DESCR_CIDADE", nullable = false)
	private String descrCidade;

	@Column(name = "UF", nullable = false)
	private String uf;

	@Column(name = "CEP", nullable = false)
	private String cep;

	@Column(name = "FONE")
	private String fone;

	@Column(name = "EMAIL", nullable = false)
	private String email;

	@Column(name = "DM_SITUACAO", nullable = false)
	private Integer dmSituacao;

	@Column(name = "DM_TP_AMB", nullable = false)
	private Integer dmTpAmb;
	
	@Column(name = "PATH_LOGOTIPO")
	private String pathLogotipo;

	@Column(name = "PATH_CERTIFICADO")
	private String pathCertificado;

	@Column(name = "SENHA_CERTIFICADO")
	private String senhaCertificado;

	@Temporal(TemporalType.DATE)
	@Column(name = "VALIDADE_CERTIFICADO")
	private Date validadeCertificado;
	
	@Column(name = "NOME_IMPRESSORA")
	private String nomeImpressora;
	
	@Column(name = "DM_IMPR_AUTO")
	private Integer dmImprAuto;
	
	@Column(name = "MAX_QTD_NF_LOTE", nullable = false)
	private BigDecimal maxQtdNfLote;
	
	@Column(name = "NRO_TENTATIVA_COMUNIC", nullable = false)
	private BigDecimal nroTentativaComunic;
	
	@Column(name = "DM_AJUSTA_TOT_NF", nullable = false)
	private Integer dmAjustaTotInf;
	
	@Column(name = "USUARIO_NFSE")
	private String usuarioNfse;
	
	@Column(name = "SENHA_NFSE")
	private String senhaNfse;
	
	@Column(name = "DM_GERA_TOT_TRIB", nullable = false)
	private Integer dmGeraTotTrib;
	
	@Column(name = "EMAIL_TEMPLATE_ID")
	private Integer emailTemplateId;

}
