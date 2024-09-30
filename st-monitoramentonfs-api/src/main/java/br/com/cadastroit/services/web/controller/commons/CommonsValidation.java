package br.com.cadastroit.services.web.controller.commons;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.api.services.NfServService;
import br.com.cadastroit.services.commons.api.NfServCommons;
import br.com.cadastroit.services.exceptions.NfServException;
import br.com.cadastroit.services.repositories.CommonsRepository;
import br.com.cadastroit.services.repositories.ConstrNfsRepository;
import br.com.cadastroit.services.repositories.FaturaNfsRepository;
import br.com.cadastroit.services.repositories.ItemNfsRepository;
import br.com.cadastroit.services.repositories.LogGenericoRepository;
import br.com.cadastroit.services.repositories.NfServRepository;
import br.com.cadastroit.services.repositories.PessoaNfsRepository;
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
	
	protected final CommonsRepository commonsRepository;
	
	protected final NfServRepository nfServRepository;
	
	protected final NfServService nfServService;
	
	protected final NfServCommons nfServCommons;
	
	protected final ConstrNfsRepository constrNfsRepository;
	
	protected final ItemNfsRepository itemNfsRepository;
	
	protected final FaturaNfsRepository faturaNfsRepository;
	
	protected final PessoaNfsRepository pessoaNfsRepository;
	
	protected final LogGenericoRepository logGenericoRepository;
	
	public <T> T recuperarObjeto(Long id, Class<T> clazz, EntityManagerFactory entityManagerFactory) {
		try {
			return this.commonsRepository.findById(id, clazz, entityManagerFactory);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NfServException(
					String.format(DbLayerMessage.NO_RESULT_POR_ID.message(), clazz.getName().toUpperCase(), "", id));
		}
	}
	
	public String createResponseTemplate(String message) {
		try {
			ResponseTemplate responseTemplate = ResponseTemplate.builder().message(message).build();
			return this.convertToJson(responseTemplate, responseTemplate.getClass());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}

	public String convertToJson(Object data, Class<? extends ResponseTemplate> clazz)
			throws NfServException {
		try {
			mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
			return ow.writeValueAsString(data);
		} catch (JsonProcessingException ex) {
			throw new NfServException(String.format("Erro na conversao do objeto %s para Json. [Erro] = %s",
					clazz.getSimpleName(), ex.getMessage()));
		}
	}
}
