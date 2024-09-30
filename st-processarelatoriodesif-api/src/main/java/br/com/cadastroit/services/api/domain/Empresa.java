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
@Table(name = "EMPRESA")
@SequenceGenerator(name = "empresa_seq", sequenceName = "empresa_seq", initialValue = 1, allocationSize = 1)
public class Empresa implements Serializable {

	private static final long serialVersionUID = 4738074728737644997L;
	
	@Id
	@GeneratedValue(generator = "empresa_seq", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	public Long id;

}
