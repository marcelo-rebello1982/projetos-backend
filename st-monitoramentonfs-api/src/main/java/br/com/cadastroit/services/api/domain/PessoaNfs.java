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
@Table(name = "PESSOA_NFS")
public class PessoaNfs implements Serializable {

	private static final long serialVersionUID = -8258744749972012191L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "NFSERV_ID", nullable = false)
	private NfServ nfServ;

	@Column(name = "COD_PART", nullable = false)
	private String codPart;

	@Column(name = "NOME_TOMADOR")
	private String nomeTomador;

	@Column(name = "LOGRAD_TOMADOR")
	private String logradTomador;

	@Column(name = "NRO_LOGRAD_TOMADOR")
	private String nroLogradTomador;

	@Column(name = "COMPL_TOMADOR")
	private String complTomador;

	@Column(name = "BAIRRO_TOMADOR")
	private String bairroTomador;

	@Column(name = "IBGE_CIDADE_TOMADOR")
	private String ibgeCidadeTomador;

	@Column(name = "CEP_TOMADOR")
	private String cepTomador;

	@Column(name = "COD_SISCOMEX_PAIS_TOMADOR")
	private String codSiscomexPaisTomador;

	@Column(name = "TELEFONE_TOMADOR")
	private String telefoneTomador;

	@Column(name = "EMAIL_TOMADOR")
	private String emailTomador;

	@Column(name = "CPF_TOMADOR")
	private String cpfTomador;

	@Column(name = "CNPJ_TOMADOR")
	private String cnpjTomador;

	@Column(name = "IM_TOMADOR")
	private String imTomador;

	@Column(name = "NIF_TOMADOR")
	private String nifTomador;
}
