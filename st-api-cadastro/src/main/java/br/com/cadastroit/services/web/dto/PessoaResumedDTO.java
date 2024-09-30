package br.com.cadastroit.services.web.dto;


import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaResumedDTO {
	
	private Long id;
	
	private String nome;

	private String fone;
	
	private String email;

	private Date dataNascimento;

	private DepartamentoResumedDTO departamento;
	
	private List<TelefoneDTO> telefone;
	
}