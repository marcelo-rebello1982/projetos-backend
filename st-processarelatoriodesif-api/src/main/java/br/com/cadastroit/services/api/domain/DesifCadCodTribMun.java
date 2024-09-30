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
@Table(name = "DESIF_CAD_COD_TRIB_MUN")
@SequenceGenerator(name = "DESIFCADCODTRIBMUN_SEQ", sequenceName = "DESIFCADCODTRIBMUN_SEQ", allocationSize = 1, initialValue = 1)
public class DesifCadCodTribMun implements Serializable {

	private static final long serialVersionUID = 3594714047937988437L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DESIFCADCODTRIBMUN_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "COD_TRIB_MUN")
	private String codTribMun;
	
	@Column(name = "ALIQUOTA")
	private BigDecimal desMista;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INI_VIG")
	private Date dtIniVig;

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_FIM_VIG")
	private Date dtFimVig;

	@Builder.Default
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_UPDATE")
	private Date dtUpdate = new Date(System.currentTimeMillis());
}
