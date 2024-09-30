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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "NOTA_FISCAL")
@SequenceGenerator(name = "NOTAFISCAL_SEQ", sequenceName = "NOTAFISCAL_SEQ", allocationSize = 1, initialValue = 1)
public class NotaFiscal implements Serializable {

    private static final long serialVersionUID = -177457125170218501L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTAFISCAL_SEQ")
    @Column(name = "ID")
    private Long id;
	
	@Column(name = "DM_ST_PROC")
	private BigDecimal dmStProc;
	
	@ManyToOne
	@JoinColumn(name = "LOTE_ID")
	private Lote lote;
	
}
