package br.com.cadastroit.services.api.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PARAMETRO")
@SequenceGenerator(name = "PARAMETRO_SEQ", sequenceName = "PARAMETRO_SEQ", allocationSize = 1)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TIPO", discriminatorType = DiscriminatorType.STRING)
public abstract class Parametro<T> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETRO_SEQ")
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Column(name = "CHAVE_STR")
	@Enumerated(EnumType.STRING)
	protected ParametroChaveType chave;
	
	// @ManyToOne
	// @JoinColumn(name = "EMPRESA_ID")
	// private Empresa empresa;
	
	public abstract T getValor();

	public abstract void setValor(T valor);

	public Long getId() {

		return id;
	}

	public void setId(Long id) {

		this.id = id;
	}

	public ParametroChaveType getChave() {

		return chave;
	}

	public void setChave(ParametroChaveType chave) {

		this.chave = chave;
	}

}
