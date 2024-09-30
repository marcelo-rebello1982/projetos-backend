package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

import br.com.cadastroit.services.api.domain.PedidoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

	private String id;
	private UUID uuid;
	private int page;
	private int lenght;
	private String nroPedido;
	private BigDecimal qtdTotal;
	private BigDecimal vlrTotal;
	private Calendar dataCompra;
	private PessoaDTO pessoa;
	private Map<String, Object> requestParams;
	private PedidoStatus status;
	private String statusStr;
	private String fileDirectory;
	private String urlTempBucket;
	private Integer statusInt;
	private Long pessoa_id;
	private String urls3;
	private String typearchive;
	private Long timeout;
	private String namearchive;
	private String creationdate;
	private Map<String, String> reference;
	private Long pessoaId;
	private Long empresaId;
	private Long desifPlanoContaId;
	private String codCta;
	private BigDecimal desMista;
	private String nome;
	private String descrCta;
	private String codCtaSup;
	private Integer[] contaReduzidaValues;
	private Integer contaReduzida;
	private Integer dmSituacao;
	private String urlS3;
	private String fileName;
	private String messages;
	private String description;
	private Long nroProtocolo;
	
	@Builder.Default
	private Boolean deleted = Boolean.FALSE;

	@Builder.Default
	private Boolean aprovado = Boolean.FALSE;

	@Builder.Default
	private Calendar dtUpdate = Calendar.getInstance();
	
}
