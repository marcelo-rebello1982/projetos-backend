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
public class ContatoDTO {

	private Long id;

	private List<EmailDTO> email;
	
	private List<TelefoneDTO> telefone;

	private Long pessoaId;

}
