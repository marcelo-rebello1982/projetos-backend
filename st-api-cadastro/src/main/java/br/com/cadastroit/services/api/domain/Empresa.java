package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "EMPRESA")
@SequenceGenerator(name = "EMPRESA_SEQ", sequenceName = "EMPRESA_SEQ", allocationSize = 1, initialValue = 1)
public class Empresa implements Serializable {

	private static final long serialVersionUID = -3339752350095644928L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMPRESA_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "COD_FILIAL")
	private String codFilial;

	@Column(name = "COD_MATRIZ")
	private String codMatriz;
    
    @OneToMany(mappedBy="empresa")
	private Set<PessoaEmpresa> pessoaEmpresa;

}
