package br.com.cadastroit.services.web.dto;
import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogGenericoDto  {

	private Long id;
	private Timestamp dtHrLog;
	private String mensagem;
	private BigDecimal referenciaId;
	private String objReferencia;
	private String resumo;
	private BigDecimal processoId;
	private CsfTipoLogDto csfTipoLog;
	private EmpresaDto empresa;
	
}