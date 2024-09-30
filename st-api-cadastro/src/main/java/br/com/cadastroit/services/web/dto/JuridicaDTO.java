package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JuridicaDTO {

	private Long id;

	private String ie;

	@NotNull(message = "Numero do CNPJ sem preenchimento")
	private BigDecimal numCnpj;

	private BigDecimal digCnpj;
}
