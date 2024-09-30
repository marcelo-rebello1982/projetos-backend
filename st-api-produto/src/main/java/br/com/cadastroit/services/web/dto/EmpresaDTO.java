package br.com.cadastroit.services.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EmpresaDTO {

	private Long id;

	private transient String codUfIBGE;

	private String codFilial;

	private String codMatriz;
	
	private PessoaDTO pessoa;

}
