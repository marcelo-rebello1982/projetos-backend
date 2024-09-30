package br.com.cadastroit.services.broker.consumers.model;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import br.com.cadastroit.services.api.domain.DesifCadCodTribMun;
import br.com.cadastroit.services.api.domain.DesifCadPcCosif;
import br.com.cadastroit.services.api.domain.Empresa;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DesifPlanoContaMessage implements Serializable {

	private static final long serialVersionUID = -9048392832054277525L;

	private Long id;

	private Empresa empresa;
	
	private String codCta;

	private BigDecimal desMista;

	private String nome;

	private String descrCta;

	private String codCtaSup;

	private DesifCadPcCosif desifCadPcCosIf;

	private DesifCadCodTribMun desifCadCodTribMun;

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