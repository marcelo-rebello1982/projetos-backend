package br.com.cadastroit.services.api.domain;

import java.math.BigDecimal;
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
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "DESIF_PLANO_CONTA")
@SequenceGenerator(name = "DESIFPLANOCONTA_SEQ", sequenceName = "DESIFPLANOCONTA_SEQ", allocationSize = 1, initialValue = 1)
public class DesifPlanoConta {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DESIFPLANOCONTA_SEQ")
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "EMPRESA_ID")
	private Empresa empresa;

	@Column(name = "COD_CTA")
	private String codCta;

	@Column(name = "DES_MISTA")
	private BigDecimal desMista;

	@Column(name = "NOME")
	private String nome;

	@Column(name = "DESCR_CTA")
	private String descrCta;

	@Column(name = "COD_CTA_SUP")
	private String codCtaSup;

	@ManyToOne
	@JoinColumn(name = "DESIFCADPCCOSIF_ID")
	private DesifCadPcCosif desifCadPcCosIf;

	@ManyToOne
	@JoinColumn(name = "DESIFCADCODTRIBMUN_ID")
	private DesifCadCodTribMun desifCadCodTribMun;
	
	@Transient
	private BigDecimal[] contaReduzidaValues;

	@Column(name = "CONTA_REDUZIDA")
	private Integer contaReduzida;

	@Column(name = "DM_SITUACAO")
	private Integer dmSituacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_UPDATE")
	private Date dtUpdate = new Date(System.currentTimeMillis());
	
	
}