package br.com.cadastroit.services.api.broker.consumers.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.web.dto.PessoaDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelatorioMessage implements Serializable {

	private static final long serialVersionUID = 7126768370521224087L;
	
    private UUID uuid;

	private String id;

	private String statusStr;

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

	private PessoaDTO pessoa;

	private String codCta;

	private BigDecimal desMista;

	private String nome;

	private String descrCta;

	private String codCtaSup;

	private Integer[] contaReduzidaValues;

	private Integer contaReduzida;

	private Integer dmSituacao;

	private Map<String, Object> requestParams;

	private int page;

	private int lenght;

	private Integer status;

	private String urlS3;

	private String fileName;

	private String messages;

	private String description;

	private Long nroProtocolo;
}