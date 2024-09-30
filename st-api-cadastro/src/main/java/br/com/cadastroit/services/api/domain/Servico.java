package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "JURIDICA")
@SequenceGenerator(name = "JURIDICA_SEQ", sequenceName = "JURIDICA_SEQ", allocationSize = 1, initialValue = 1)
public class Servico implements Serializable {

	private static final long serialVersionUID = 3674857151123576212L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JURIDICA_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "NUM_CNPJ", nullable = false)
	private BigDecimal numCnpj;

	@Column(name = "NUM_FILIAL", nullable = false)
	private BigDecimal numFilial;

	@Column(name = "DIG_CNPJ", nullable = false)
	private BigDecimal digCnpj;

	@Column(name = "IE", nullable = false)
	private String ie;

	@ManyToOne
	@JoinColumn(name = "PESSOA_ID", nullable = false)
	private Pessoa pessoa;

	@Transient
	public String getCNPJFormatado() {

		if (numCnpj != null && numFilial != null && digCnpj != null) {
			String cnpj = getCNPJConcatenado();
			StringBuilder sb = new StringBuilder();
			sb.append(StringUtils.substring(cnpj, 0, 2)).append(".");
			sb.append(StringUtils.substring(cnpj, 2, 5)).append(".");
			sb.append(StringUtils.substring(cnpj, 5, 8)).append("/");
			sb.append(StringUtils.substring(cnpj, 8, 12)).append("-");
			sb.append(StringUtils.substring(cnpj, 12, 14));
			return sb.toString();
		} else {
			return "";
		}
	}

	@Transient
	public String getCNPJConcatenado() {

		if (numCnpj != null && numFilial != null && digCnpj != null) {
			return StringUtils.leftPad(this.numCnpj.toString(), 8, "0")
					.concat(StringUtils.leftPad(this.numFilial.toString(), 4, "0"))
					.concat(StringUtils.leftPad(this.digCnpj.toString(), 2, "0"));
		} else {
			return "";
		}
	}
}
