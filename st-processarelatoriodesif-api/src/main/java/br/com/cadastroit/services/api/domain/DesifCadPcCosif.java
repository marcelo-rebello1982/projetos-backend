package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
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
@Table(name = "DESIF_CAD_PC_COSIF")
@SequenceGenerator(name = "DESIFCADPCCOSIF_SEQ", sequenceName = "DESIFCADPCCOSIF_SEQ", allocationSize = 1, initialValue = 1)
public class DesifCadPcCosif implements Serializable {

	private static final long serialVersionUID = -8459183387926738478L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DESIFCADPCCOSIF_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "COD_CTA")
	private String codCta;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_CRIACAO")
	private Date dtCriacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_EXTINCAO")
	private Date dtExtincao;

	@Column(name = "COD_CTA_SUP")
	private String codCtaSup;

	@Column(name = "NOME_CTA")
	private String nomeConta;

	@Column(name = "DESCR_FUNC_CTA")
	private String descrFuncCta;

	@Builder.Default
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_UPDATE")
	private Date dtUpdate = new Date(System.currentTimeMillis());

}
