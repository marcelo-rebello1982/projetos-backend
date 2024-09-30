package br.com.cadastroit.services.web.dto;

import br.com.cadastroit.services.api.domain.Logradouro;
import br.com.cadastroit.services.api.enums.TipoEndereco;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EnderecoDTO  {

	private Long id;

	private String endereco;

	private String numero;

	private String complemento;

	private String bairro;
	
	private Long pessoaId;
	
	private Logradouro logradouro;
	
	private TipoEndereco tipoEndereco;

}
