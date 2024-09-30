package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "CONSTR_NFS")
public class ConstrNfs implements Serializable {

	private static final long serialVersionUID = 2332594626486637261L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "NFSERV_ID", nullable = false)
	private NfServ nfServ;

	@Column(name = "COD_OBRA")
	private String codObra;

	@Column(name = "NRO_ART")
	private String nroArt;

	@Column(name = "NRO_CNO")
	private String nroCno;

	@Column(name = "DM_IND_OBRA", nullable = false)
	private BigDecimal dmIndObra;
}
