package br.com.cadastroit.services.web.mapper;

import org.springframework.stereotype.Component;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.web.dto.ResponsePessoaDTO;

@Component
public class RetornoPessoaMapper {

	public ResponsePessoaDTO toDto(Pessoa entity) {

		ResponsePessoaDTO response = new ResponsePessoaDTO();

		// response.setCnpjEmpresa(retorno.getJuridica().getCNPJConcatenado());

		return response;
	}
	
}
