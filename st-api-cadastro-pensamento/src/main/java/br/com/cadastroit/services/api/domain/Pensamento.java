package br.com.cadastroit.services.api.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "PENSAMENTO")
@SequenceGenerator(name = "PENSAMENTO_SEQ", sequenceName = "PENSAMENTO_SEQ", allocationSize = 1, initialValue = 1)
public class Pensamento implements Serializable {
	
	private static final long serialVersionUID = -4780655990392674841L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PENSAMENTO_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "CONTEUDO")
	private String conteudo;
	
	@Column(name = "AUTORIA")
	private String autoria;
	
	@Column(name = "MODELO")
	private String modelo;
	
	@Column(name = "FAVORITO")
	private boolean favorito = false;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;

}
