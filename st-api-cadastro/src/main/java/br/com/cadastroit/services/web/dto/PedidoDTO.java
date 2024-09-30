package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.cadastroit.services.api.domain.PedidoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO  {

	private Long id;

	private String nroPedido;

	private BigDecimal qtdTotal;

	private BigDecimal vlrTotal;

    // private Boolean deleted;
	
    private Boolean aprovado;

    private LocalDateTime dataCompra;

    private LocalDateTime dtUpdate;

	private PedidoStatus status;
	
	private PessoaDTO pessoa;
	
	private Map<String, String> requestParams;
	
}
