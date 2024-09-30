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
public class ItemResumeDTO {

	private Long id;

	private BigDecimal aliqIcms;

	private String codBarra;

	private String codItem;

	private String descrItem;

	private String codAntItem;
	
	private BigDecimal quantidade;
	
	private Long empresaId;
	
}
