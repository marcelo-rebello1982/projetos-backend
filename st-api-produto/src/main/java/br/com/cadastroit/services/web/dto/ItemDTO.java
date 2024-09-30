package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

	private Long id;

	@NotNull(message = "aliqIcms do item sem preenchimento")
	private BigDecimal aliqIcms;

	@NotNull(message = "Codigo codBarra do item sem preenchimento")
	private String codBarra;

	@NotNull(message = "Codigo codItem complementar do item sem preenchimento")
	private String codItem;

	@NotNull(message = "Codigo descrItem complementar do item sem preenchimento")
	private String descrItem;

	@Size(max = 100, message = "Campo codAntItem não pode ultrapassar o tamanho máximo de 100 caracteres")
	private String codAntItem;
	
	@NotNull(message = "Campo quantidade do item sem preenchimento")
	private BigDecimal quantidade;

	@NotNull(message = "empresa sem preenchimento")
	private EmpresaDTO empresa;
	
}
