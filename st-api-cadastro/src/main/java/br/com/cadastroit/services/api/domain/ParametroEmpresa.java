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
import javax.persistence.OneToOne;
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
@Table(name = "PARAMETROEMPRESA")
@SequenceGenerator(name = "PARAMETROEMPRESA_SEQ", sequenceName = "PARAMETROEMPRESA_SEQ", allocationSize = 1)
public class ParametroEmpresa  implements Serializable {

	private static final long serialVersionUID = 2480089905516469846L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PARAMETROENTIDADE_SEQ")
	@Column(name = "ID")
	private Long id;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "EMPRESA_ID", referencedColumnName = "ID")
	private Empresa empresa;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "PARAMETRO_ID", referencedColumnName = "ID")
	private Parametro parametro;

}
