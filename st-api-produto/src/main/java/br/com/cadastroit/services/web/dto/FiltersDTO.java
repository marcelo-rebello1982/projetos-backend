package br.com.cadastroit.services.web.dto;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltersDTO {

	private BigDecimal[] dmStProc;
	private String token;
	private String serie;
	private String dtHrEntSist;
	private String codMod;
	private Integer dmStEmail;
	private Long empresaId;
	private String[] idsConsolidar;
	
	private int status;

	private String descricao;

	private Date dataInicial;

	private Date dataFinal;
	
	private String protocolo;
	
	private List<String> dataFilters;
	
	private transient Integer filters;
}
