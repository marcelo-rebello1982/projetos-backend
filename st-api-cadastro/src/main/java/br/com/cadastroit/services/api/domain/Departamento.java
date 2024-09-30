package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "DEPARTAMENTO")
@SequenceGenerator(name = "DEPARTAMENTO_SEQ", sequenceName = "DEPARTAMENTO_SEQ", allocationSize = 1, initialValue = 1)
public class Departamento implements Serializable {


	private static final long serialVersionUID = -7621545790334176963L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEPARTAMENTO_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DESCR")
	private String descr;
	
    @OneToMany(mappedBy = "departamento", fetch = FetchType.EAGER)
    private List<Pessoa> pessoas;
	
    @OneToMany(mappedBy = "departamento", fetch = FetchType.EAGER)
    private List<Tarefa> tarefas;
	
	@Transient
	public Long quantidadePessoas;
	
	@Transient
	public Long quantidadeTarefas;

}
