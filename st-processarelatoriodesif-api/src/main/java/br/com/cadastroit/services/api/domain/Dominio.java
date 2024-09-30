package br.com.cadastroit.services.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "DOMINIO")
@SequenceGenerator(name = "DOMINIO_SEQ", sequenceName = "DOMINIO_SEQ", allocationSize = 1, initialValue = 1)
public class Dominio implements Serializable {

	private static final long serialVersionUID = -5794341532176159197L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DOMINIO_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "DESCR")
	private String descr;

	@Column(name = "DOMINIO")
	private String dominio;

	@Column(name = "VL")
	private String vl;
}