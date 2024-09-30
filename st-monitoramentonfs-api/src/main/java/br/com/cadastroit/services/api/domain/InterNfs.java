package br.com.cadastroit.services.api.domain;

import java.io.Serializable;

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
@Table(name = "INTER_NFS")
public class InterNfs implements Serializable {

	private static final long serialVersionUID = -2787613982882785302L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "NFSERV_ID")
	private NfServ nfServ;

	@Column(name = "NOME_INTERMED", nullable = false)
	private String nomeIntermed;

	@Column(name = "IM_INTERMED", nullable = false)
	private String imIntermed;

	@Column(name = "CPF_INTERMED")
	private String cpfIntermed;

	@Column(name = "CNPJ_INTERMED")
	private String cnpjIntermed;

	@Column(name = "IBGE_INTERMED", nullable = false)
	private String ibgeIntermed;
}
