package br.com.cadastroit.services.web.controllers.commons;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.enums.DbLayerMessage;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.ItemRepository;
import br.com.cadastroit.services.web.mapper.EmpresaMapper;
import br.com.cadastroit.services.web.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ApiProdutoCommonsController {

	@Autowired
	public EntityManagerFactory entityManagerFactory;

	@Autowired
	protected final ObjectMapper mapperJson;

	protected final String EMPTY_MSG = "List is empty...";

	protected final EmpresaRepository empresaRepository;
	protected final ItemRepository itemRepository;

	protected final EmpresaMapper empresaMapper = Mappers.getMapper(EmpresaMapper.class);
	protected final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

	protected ResponseEntity<Object> validarCabecalho(String uuid, String token) {

		boolean valido = (uuid != null && !uuid.equals("")) && (token != null && !token.equals(""));

		return !valido ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(DbLayerMessage.EMPTY_MSG.message()) : null;
	}

	protected ResponseEntity<Object> validarCollection(List<?> collection) {

		return collection.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(DbLayerMessage.EMPTY_MSG.message())
				: ResponseEntity.status(HttpStatus.OK).body(collection);
	}

	protected <T, D> D mountObject(Function<T, D> mapper, T entity) {

		return mapper.apply(entity);
	}
	
	public <T> T convertFromJson(String json, Class<T> clazz) throws JsonProcessingException {
		mapperJson.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapperJson.readValue(json, clazz);
	}
	
	public Object parserJson(String json, Class<?> clazz) throws IOException {
		mapperJson.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
		ObjectReader ow = mapperJson.reader();
		JsonParser jsonParser = null;
		jsonParser = ow.createParser(json);
		jsonParser.close();
		return jsonParser.readValueAs(clazz);
	}
	
	public String writeJsonBase64(Object value) throws JsonProcessingException {
		mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
		String jsonDto = ow.writeValueAsString(value);
		String base64 = Base64.getEncoder().encodeToString(jsonDto.getBytes());
		return base64;
	}
	
	public String writeJson(Object value) throws JsonProcessingException {
		mapperJson.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapperJson.writer().withDefaultPrettyPrinter();
		return ow.writeValueAsString(value);
	}

	protected <T, R> List<R> mountObject(Function<T, R> mapper, List<T> list) {

		return list.stream().map(mapper).collect(Collectors.toList());
	}

	public boolean validateStatusTarefa(BigDecimal status) {

		return new HashSet<>(Arrays.asList(1L)).contains(status.longValue());
	}

	protected HttpHeaders httpHeaders(String count) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("summaryCount", count);
		return headers;
	}

	protected HttpHeaders httpHeaders(String count, Long id) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("summaryCount", count);
		headers.add("empresaId", String.valueOf(id));
		return headers;
	}
}
