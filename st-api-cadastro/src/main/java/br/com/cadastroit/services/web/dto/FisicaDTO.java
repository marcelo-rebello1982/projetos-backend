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
public class FisicaDTO {

	private Long id;
	
	private BigDecimal digCpf;
	
	private BigDecimal numCpf;
	
	private String rg;
	
	private String CPFConcatenado;
	
	private String CPFFormatado;

}
