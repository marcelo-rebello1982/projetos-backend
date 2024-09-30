package br.com.cadastroit.services.api.domain;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
public class Juridica implements Serializable {
	
	private static final long serialVersionUID = 2860063938986252591L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "JURIDICA_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "IE")
	private String ie;
	
	@Column(name = "IEST")
	private String iest;
	
	@Column(name = "IM")
	private String im;
	
	@Column(name = "NUM_CNPJ", unique = true)
	private BigDecimal numCnpj;
	
	@Column(name = "NUM_FILIAL")
	private BigDecimal numFilial;
	
	@Column(name = "DIG_CNPJ", precision = 19, scale = 4)
	private BigDecimal digCnpj;
	
	@OneToOne
	@JoinColumn(name = "PESSOAEMPRESA_ID", nullable = false)
	private PessoaEmpresa pessoaEmpresa;

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

	@Transient
	public String getCNPJFormatado() {

		if (numCnpj != null && numFilial != null && digCnpj != null) {
			String cnpj = getCNPJConcatenado();
			StringBuffer sb = new StringBuffer();
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

	public void setCNPJFormatado(String cnpj) {

		if (cnpj != null && !cnpj.isEmpty()) {
			StringBuilder aux = new StringBuilder();
			int i = 0;
			while (i < cnpj.length()) {
				try {
					Integer v = Integer.parseInt(cnpj.substring(i, i + 1));
					aux.append(v);
				} catch (Exception e) {
					;
				}
				i++;
			}

			// Parser do cnpj para os campos
			if (aux.length() == 8) {
				numCnpj = new BigDecimal(aux.toString());
			} else {
				if (aux.length() == 12) {
					numCnpj = new BigDecimal(aux.substring(0, 8));
					numFilial = new BigDecimal(aux.substring(8, 12));
				} else {
					if (aux.length() == 14) {
						numCnpj = new BigDecimal(aux.substring(0, 8));
						numFilial = new BigDecimal(aux.substring(8, 12));
						digCnpj = new BigDecimal(aux.substring(12, 14));
					} else {
						numCnpj = null;
						numFilial = null;
						digCnpj = null;
					}
				}
			}
		} else {
			numCnpj = null;
			numFilial = null;
			digCnpj = null;
		}
	}
	
}
