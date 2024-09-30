package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "FATURA_NFS")
public class FaturaNfs implements Serializable {

	private static final long serialVersionUID = 9027710849242595397L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "NFSERV_ID", nullable = false)
	private NfServ nfServ;
	
	@Column(name = "NRO_FATURA")
	private String nroFatura;
	
	@Column(name = "NRO_PARCELA")
	private String nroParcela;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DT_VENCTO")
	private Date dtVencto;
	
	@Column(name = "VL_DUPLICATA")
	private BigDecimal vlDuplicata;
}
