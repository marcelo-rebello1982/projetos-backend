package br.com.cadastroit.services.api.domain;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CSF_TIPO_LOG")
@SequenceGenerator(name = "CSFTIPOLOG_SEQ", sequenceName = "CSFTIPOLOG_SEQ", allocationSize = 1, initialValue = 1)
public class CsfTipoLog implements Serializable {
	
	private static final long serialVersionUID = -5824814306982948172L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CSFTIPOLOG_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "CD")
	private String cd;
	
	@Column(name = "DESCR")
	private String descr;
	
	@Column(name = "DM_GRAU_SEV")
	private BigDecimal dmGrauSeveridade;
}