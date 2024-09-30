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
@Table(name = "PARAMETROBOOLEAN")
@DiscriminatorValue(value = "B")
public final class ParametroBoolean extends Parametro<Boolean> implements Serializable {

	private static final long serialVersionUID = -2242616248926546441L;
	
	@NotNull
	@Column(name = "VALOR")
	private Boolean valor = Boolean.FALSE;

	public ParametroBoolean(ParametroChaveType chave) {

		this.chave = chave;
	}

	public Boolean getValor() {

		return this.valor;
	}

	public void setValor(Boolean valor) {

		this.valor = valor;

	}
}
