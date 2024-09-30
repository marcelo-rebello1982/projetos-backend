package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@SequenceGenerator(name = "VENDA_SEQ", sequenceName = "VENDA_SEQ", allocationSize = 1)
@SQLDelete(sql = "update {database_user}.venda set excluida = '1', dataAlteracao = sysdate, dataExclusao = sysdate where id = ?")
@Where(clause = "excluida = '0'")
public class Venda implements Serializable {

	private static final long serialVersionUID = -7231589450263383605L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VENDA_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DESCONTO")
	private BigDecimal desconto;

	@Column(name = "ACRESCIMO")
	private BigDecimal acrescimo;
	
	@Column(name = "TAXA_FINANCEIRA")
	private BigDecimal taxaFinanceira;

	private Calendar dataLancamento;
	
	private boolean excluida;

	@ManyToOne
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
	

}
