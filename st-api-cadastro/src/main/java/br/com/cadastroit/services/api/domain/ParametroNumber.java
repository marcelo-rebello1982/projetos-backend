package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(name = "PARAMETRONUMBER")
@DiscriminatorValue("N")
public final class ParametroNumber extends Parametro<Number> implements Serializable {

	private static final long serialVersionUID = 2534778518165678577L;

	@NotNull
	@Column(name = "VALOR")
	private BigDecimal valor;

	public ParametroNumber(ParametroChaveType chave) {

		setChave(chave);
		setValor(0);
	}

	public ParametroNumber(ParametroChaveType chave, Number valor) {

		this(chave);
		setValor(valor);
	}

	public Number getValor() {

		return this.valor;
	}

	public void setValor(Number valor) {

		this.valor = new BigDecimal(valor.toString());

	}
}
