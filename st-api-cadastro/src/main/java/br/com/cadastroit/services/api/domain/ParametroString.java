package br.com.cadastroit.services.api.domain;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
@Table(name = "PARAMETROSTRING")
@DiscriminatorValue("S")
public class ParametroString extends Parametro<String> implements Serializable {

	private static final long serialVersionUID = -3990594015380920152L;

	@Column(name = "VALOR")
	private String valor;

	public ParametroString(ParametroChaveType chave) {

		setChave(chave);
	}

	public ParametroString(ParametroChaveType chave, String valor) {

		this(chave);
		setValor(valor);
	}

	public String getValor() {

		return this.valor;
	}

	public void setValor(String valor) {

		this.valor = valor;

	}

}
