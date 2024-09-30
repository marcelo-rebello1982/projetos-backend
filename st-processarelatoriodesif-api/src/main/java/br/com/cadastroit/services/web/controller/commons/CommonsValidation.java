package br.com.cadastroit.services.web.controller.commons;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.domain.MultOrg;
import br.com.cadastroit.services.exceptions.DesifPlanoContaException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CommonsValidation {

	private final ObjectMapper mapperJson = new ObjectMapper();

	public String createResponseTemplate(String message) {
		try {
			ResponseTemplate responseTemplate = ResponseTemplate.builder().message(message).build();
			return this.convertToJson(responseTemplate, responseTemplate.getClass());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}

	public String createResponseTemplate(String message, Long protocolo) {
		try {
			ResponseTemplate responseTemplate = ResponseTemplate.builder().nroProtocolo(protocolo).message(message)
					.build();
			String json = this.convertToJson(responseTemplate, responseTemplate.getClass());
			return json;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}

	public String convertToJson(Object data, Class<? extends ResponseTemplate> clazz) throws DesifPlanoContaException {
		try {
			mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
			return ow.writeValueAsString(data);
		} catch (JsonProcessingException ex) {
			throw new DesifPlanoContaException(String.format("Erro na conversao do objeto %s para Json. [Erro] = %s",
					clazz.getSimpleName(), ex.getMessage()));
		}
	}

	protected boolean validarAcessoRegistro(String cd, String hash, MultOrg multOrg) {
		String cdMultOrg = multOrg.getCd();
		String hashMultOrg = multOrg.getHash();
		return (cdMultOrg.equals(cd) && hashMultOrg.equals(hash));
	}
}
