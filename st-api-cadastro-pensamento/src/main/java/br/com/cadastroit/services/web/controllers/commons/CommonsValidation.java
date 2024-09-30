package br.com.cadastroit.services.web.controllers.commons;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.repositories.PensamentoRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@AllArgsConstructor
public class CommonsValidation {

	private final ObjectMapper mapperJson = new ObjectMapper();
	
	
	private PensamentoRepository pensamentoRepository;
	
	private PessoaRepository pessoaRepository;

	
	
	public Pessoa recuperarPessoa(Long id, EntityManagerFactory entityManager) {
		try {
			return pessoaRepository.findById(id, entityManager);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
		}
	}
	
	
	public String createResponseTemplate(String message) {
	    try {
	        ResponseTemplate responseTemplate = ResponseTemplate.builder().message(message).build();
	        return convertToJson(responseTemplate, responseTemplate.getClass());
	    } catch (JsonProcessingException ex) {
	        log.error(ex.getMessage());
	        return null;
	    }
	}

	public String convertToJson(Object data, Class<? extends ResponseTemplate> clazz) throws JsonProcessingException {
		mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(data);
	}
}
