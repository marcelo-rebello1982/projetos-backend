package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.cadastroit.services.api.enums.Status;
import br.com.cadastroit.services.api.enums.TipoDocumento;
import br.com.cadastroit.services.api.enums.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PESSOA")
@SequenceGenerator(name = "PESSOA_SEQ", sequenceName = "PESSOA_SEQ", allocationSize = 1, initialValue = 1)
public class Pessoa implements Serializable {

	private static final long serialVersionUID = 272755268202589678L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PESSOA_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NOME")
	private String nome;

	@Column(name = "FONE")
	private String fone;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "COD_PART")
	private String codPart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_NASCIMENTO")
	private Date dataNascimento;

	@ManyToOne
	@JoinColumn(name = "DEPARTAMENTO_ID")
	private Departamento departamento;
	
	@CreationTimestamp
	private LocalDateTime createAt;
	
	@UpdateTimestamp
	private LocalDateTime updateAt;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPODOCUMENTO")
	private TipoDocumento tipoDocumento;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TIPOPESSOA")
	private TipoPessoa tipoPessoa;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	// @OrderBy(value = "id desc")
	// @OneToMany(mappedBy = "pessoa",  cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// private List<Tarefa> tarefas;
	
	// @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// private List<Endereco> enderecos;
	
	// @OneToMany(mappedBy = "pessoa",  cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// private List<Telefone> telefone;
	
	// @OrderBy(value = "id desc")
	// @OneToMany(mappedBy = "pessoa",  cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	// private List<Email> emails;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "PESSOA_ID", referencedColumnName = "ID")
	private List<Tarefa> tarefas;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "PESSOA_ID", referencedColumnName = "ID")
	private List<Endereco> enderecos;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "PESSOA_ID", referencedColumnName = "ID")
	private List<Telefone> telefone;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "PESSOA_ID", referencedColumnName = "ID")
	private List<Email> emails;
	
	@Transient
	public Double mediaDeHorasPorTarefa;
}