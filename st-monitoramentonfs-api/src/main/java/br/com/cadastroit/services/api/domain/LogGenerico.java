package br.com.cadastroit.services.api.domain;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "LOG_GENERICO")
@SequenceGenerator(name = "LOGGENERICO_SEQ", sequenceName = "LOGGENERICO_SEQ", allocationSize = 1, initialValue = 1)
public class LogGenerico implements Serializable {

	private static final long serialVersionUID = -4051254132001260421L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "LOGGENERICO_SEQ")
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "DT_HR_LOG")
	private Timestamp dtHrLog;
	
	@Column(name = "MENSAGEM")
	private String mensagem;
	
	@Column(name = "REFERENCIA_ID")
	private BigDecimal referenciaId;
	
	@Column(name = "OBJ_REFERENCIA")
	private String objReferencia;
	
	@Column(name = "RESUMO")
	private String resumo;
	
	@Column(name = "PROCESSO_ID")
	private BigDecimal processoId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CSFTIPOLOG_ID")
	private CsfTipoLog csfTipoLog;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "EMPRESA_ID")
	private Empresa empresa;
}