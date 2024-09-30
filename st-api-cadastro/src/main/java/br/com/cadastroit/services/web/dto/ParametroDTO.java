package br.com.cadastroit.services.web.dto;

import br.com.cadastroit.services.api.domain.ParametroChaveType;
import br.com.cadastroit.services.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParametroDTO {

	private Long id;

	private Object valor;

	private ParametroChaveType chave;

	public boolean hasValor() {

		return this.getValor() != null && StringUtils.isNotBlank(this.getValor().toString());
	}

}
