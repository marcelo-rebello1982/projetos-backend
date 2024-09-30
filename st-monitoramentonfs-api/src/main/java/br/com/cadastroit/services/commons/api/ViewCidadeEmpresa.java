package br.com.cadastroit.services.commons.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewCidadeEmpresa  {

	private String ibgeCidade;
	private String descrCidade;
	private Integer dmDbDestino;
	private String uf;

}
