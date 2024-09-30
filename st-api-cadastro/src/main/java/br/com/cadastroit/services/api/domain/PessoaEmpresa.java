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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PESSOAEMPRESA")
@SequenceGenerator(name = "PESSOAEMPRESA_SEQ", sequenceName = "PESSOAEMPRESA_SEQ", allocationSize = 1, initialValue = 1)
public class PessoaEmpresa implements Serializable {

	private static final long serialVersionUID = -4882043865518298021L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PESSOAEMPRESA_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "NOME")
	private String nome;

	@Column(name = "FONE")
	private String fone;

	@Column(name = "EMAIL", length = 300)
	private String email;

	@OneToOne(mappedBy = "pessoaEmpresa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Juridica juridica;

	@OneToOne(mappedBy = "pessoaEmpresa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Fisica fisica;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EMPRESA_ID")
	@Cascade(value = org.hibernate.annotations.CascadeType.MERGE)
	private Empresa empresa;

}