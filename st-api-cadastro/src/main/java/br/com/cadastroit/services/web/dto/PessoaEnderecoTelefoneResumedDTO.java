package br.com.cadastroit.services.web.dto;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaEnderecoTelefoneResumedDTO {
	
	private Long id;
	
	private String nome;

	private String email;

	private String fone;
	
	private List<EnderecoDTO> enderecos;
	
	private List<TelefoneDTO> telefone;

	
}