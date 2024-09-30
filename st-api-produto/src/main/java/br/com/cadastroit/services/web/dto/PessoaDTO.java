package br.com.cadastroit.services.web.dto;

import java.util.Date;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaDTO {

	private Long id;

	@Size(min = 1, max = 255, message = "Campo nome não pode ultrapassar o total de 255 caracteres.")
	private String nome;

	private String email;

	private String fone;

	private Date dataNascimento;

	private String numeroTelefone;

}