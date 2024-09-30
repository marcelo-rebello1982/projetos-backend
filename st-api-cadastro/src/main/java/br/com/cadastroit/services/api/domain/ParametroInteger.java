package br.com.cadastroit.services.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PARAMETROINTEGER")
@DiscriminatorValue("I")
public final class ParametroInteger extends Parametro<Integer> implements Serializable {

	private static final long serialVersionUID = -8658475128685921078L;

	@NotNull
	@Column(name = "VALOR")
	private Integer valor;

	public ParametroInteger(ParametroChaveType chave) {

		this.chave = chave;
	}

	public Integer getValor() {

		return this.valor;
	}

	public void setValor(Integer valor) {

		this.valor = valor;
	}

}
