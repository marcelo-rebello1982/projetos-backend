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
@Table(name = "EMAIL")
@SequenceGenerator(name = "EMAIL_SEQ", sequenceName = "EMAIL_SEQ", allocationSize = 1, initialValue = 1)
public class Email implements Serializable {

	private static final long serialVersionUID = 74097158098301520L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "TIPO")
	private int tipo;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
}
