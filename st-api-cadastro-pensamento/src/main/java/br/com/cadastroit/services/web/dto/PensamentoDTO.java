package br.com.cadastroit.services.web.dto;

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
public class PensamentoDTO  {
	
	private Long id;
	
	@NotNull(message = "Atenção : campo conteúdo sem preenchimento")
	@Size(min = 1, max = 255, message = "O numero de caracteres deve ser maior ou igual a 1 e menor ou igual a 255 para o campo conteudo")
	private String conteudo;

	@NotNull(message = "Atenção : campo autoria sem preenchimento")
	@Size(min = 1, max = 50, message = "O numero de caracteres deve ser maior ou igual a 1 e menor ou igual a 50 para o campo autoria")
	private String autoria;

	@NotNull(message = "Atenção : campo modelo sem preenchimento")
	private String modelo;
	
	private PessoaDTO pessoa;
	
	private boolean favorito;
	

}
