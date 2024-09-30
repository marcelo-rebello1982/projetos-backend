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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CIDADE")
@SequenceGenerator(name = "CIDADE_SEQ", sequenceName = "CIDADE_SEQ", allocationSize = 1, initialValue = 1)
public class Cidade implements Serializable {

	private static final long serialVersionUID = -6201046720045138747L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CIDADE_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DESCR")
	private String descr;
	
}