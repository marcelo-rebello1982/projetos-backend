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
@Table(name = "FISICA")
@SequenceGenerator(name = "FISICA_SEQ", sequenceName = "FISICA_SEQ", allocationSize = 1, initialValue = 1)
public class Fisica implements Serializable {
	
	private static final long serialVersionUID = 4135668073294412957L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "FISICA_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DIG_CPF")
	private BigDecimal digCpf;
	
	@Column(name = "NUM_CPF")
	private BigDecimal numCpf;
	
	@Column(name = "RG")
	private String rg;
	
	@OneToOne
	@JoinColumn(name = "PESSOAEMPRESA_ID", nullable = false)
	private PessoaEmpresa pessoaEmpresa;
	
	@Transient
	public String getCPFFormatado() { // 277.691.578-.003.00 // .003 0075
		String cpf = getCPFConcatenado();
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.substring(cpf, 0, 3)).append(".");
		sb.append(StringUtils.substring(cpf, 3, 6)).append(".");
		sb.append(StringUtils.substring(cpf, 6, 9)).append("-");
		sb.append(String.format("%02d",new BigDecimal(StringUtils.substring(cpf, 9, 13)).intValue()));
		return sb.toString();
	}
	
	@Transient
	public String getCPFConcatenado() {
		return StringUtils.leftPad(this.numCpf.toString(), 9, "0")
			.concat(StringUtils.leftPad(this.digCpf.toString(), 2, "0"));
	}
	
	public void setCPFFormatado(String cpf){
		
		if(cpf != null && !cpf.isEmpty()){
			// Retira todo tipo de caracteres
			StringBuilder aux = new StringBuilder();
			int i = 0;
			while(i < cpf.length()){
				try{
					Integer v = Integer.parseInt(cpf.substring(i, i+1));
					aux.append(v);
				}catch(Exception e){;}
				i++;
			}
	
			if(aux.length() == 9){
				numCpf = new BigDecimal(aux.toString());
			}else{
				if(aux.length() == 11){
					numCpf = new BigDecimal(aux.substring(0, 9));
					digCpf = new BigDecimal(aux.substring(9, 11));
				}
			}
		}else{
			numCpf = null;
			digCpf = null;
		}
	}
	
}
