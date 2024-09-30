package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "PEDIDO")
@SequenceGenerator(name = "PEDIDO_SEQ", sequenceName = "PEDIDO_SEQ", allocationSize = 1, initialValue = 1)
public class Pedido implements Serializable {

	private static final long serialVersionUID = 7338036223525780370L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PEDIDO_SEQ")
	@Column(name = "ID")
	private Long id;

	@Column(name = "NRO_PEDIDO")
	private String nroPedido;

	@Column(name = "QTD_TOTAL")
	private BigDecimal qtdTotal;

	@Column(name = "VLR_TOTAL")
	private BigDecimal vlrTotal;

	@Builder.Default
	@Column(name = "DELETED", nullable = false)
	private Boolean deleted = Boolean.FALSE;

	@Builder.Default
	@Column(name = "APROVADO", nullable = false)
	private Boolean aprovado = Boolean.FALSE;

	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dataCompra;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PedidoStatus status;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
}
