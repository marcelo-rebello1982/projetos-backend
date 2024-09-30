package br.com.cadastroit.services.broker.consumers.model;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import br.com.cadastroit.services.web.dto.EmpresaDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelatorioMessage {

	private Long id;
	private EmpresaDTO empresa;
	private String codCta;
	private BigDecimal desMista;
	private String nome;
	private String descrCta;
	private String codCtaSup;
	private Integer[] contaReduzidaValues;
	private Integer contaReduzida;
	private Integer dmSituacao;
	private File file;
	private UUID uuid;
	private String fileDirectory;
	private String fileName;
	private String urlTempBucket;
	private String nroProtocolo;
	private Date dtUpdate = new Date(System.currentTimeMillis());

}