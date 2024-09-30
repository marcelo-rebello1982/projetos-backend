package br.com.cadastroit.services.web.controllers.commons;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.api.services.ProcessaPedidoService;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.repositories.ProcessaPedidoRepository;
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

	private ProcessaPedidoRepository processaPedidoRepository;
	private ProcessaPedidoService processaPedidoService;

	public Pedido recuperarPedido(Long id, EntityManagerFactory entityManager) {

		try {
			return processaPedidoRepository.findById(id, entityManager);
		} catch (NoResultException | NonUniqueResultException ex) {
			throw new NoResultException(ex.getMessage());
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

	public String convertToJson(Object data, Class<? extends ResponseTemplate> clazz) throws PedidoException {

		try {
			mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
			return ow.writeValueAsString(data);
		} catch (JsonProcessingException ex) {
			throw new PedidoException(String.format("Erro na conversao do objeto %s para Json. [Erro] = %s", clazz.getSimpleName(), ex.getMessage()));
		}
	}

	public String createResponseTemplate(String message, Long protocolo) {

		try {
			ResponseTemplate responseTemplate = ResponseTemplate.builder().message(message).nroProtocolo(protocolo).build();
			String json = this.convertToJson(responseTemplate, responseTemplate.getClass());
			return json;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			return null;
		}
	}
}
