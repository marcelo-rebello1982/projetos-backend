package br.com.cadastroit.services.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dataCompra;

    @UpdateTimestamp
    private LocalDateTime dtUpdate;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PedidoStatus status = PedidoStatus.EM_ANDAMENTO;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PESSOA_ID")
	private Pessoa pessoa;
}
