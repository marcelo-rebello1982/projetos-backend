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
@Table(name = "CONTATO")
@SequenceGenerator(name = "CONTATO_SEQ", sequenceName = "CONTATO_SEQ", allocationSize = 1, initialValue = 1)
public class Contato implements Serializable {

	private static final long serialVersionUID = 6860127476758388921L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTATO_SEQ")
	@Column(name = "ID")
	private Long id;

	

}
