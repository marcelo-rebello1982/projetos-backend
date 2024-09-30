package br.com.cadastroit.services.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PessoaEmpresaDTO  {

	private Long id;

	private String nome;

	private String fone;

	private String email;

	private JuridicaDTO juridica;

	private FisicaDTO fisica;

	private EmpresaDTO empresa;

}