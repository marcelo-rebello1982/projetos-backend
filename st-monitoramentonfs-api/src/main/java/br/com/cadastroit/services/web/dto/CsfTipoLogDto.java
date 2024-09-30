package br.com.cadastroit.services.web.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CsfTipoLogDto  {
	
	private Long id;
	private String cd;
	private String descr;
	private BigDecimal dmGrauSeveridade;
}