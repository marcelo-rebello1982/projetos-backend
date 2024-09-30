package br.com.cadastroit.services.web.controllers.commons;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
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
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.repositories.DepartamentoRepository;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.EnderecoRepository;
import br.com.cadastroit.services.repositories.ParametroRepository;
import br.com.cadastroit.services.repositories.PedidoRepository;
import br.com.cadastroit.services.repositories.PessoaEmpresaRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.repositories.TarefaRepository;
import br.com.cadastroit.services.repositories.TelefoneRepository;
import br.com.cadastroit.services.web.dto.FiltersDTO;
import br.com.cadastroit.services.web.mapper.DepartamentoMapper;
import br.com.cadastroit.services.web.mapper.EmpresaMapper;
import br.com.cadastroit.services.web.mapper.EnderecoMapper;
import br.com.cadastroit.services.web.mapper.ParametroMapper;
import br.com.cadastroit.services.web.mapper.PedidoMapper;
import br.com.cadastroit.services.web.mapper.PessoaEmpresaMapper;
import br.com.cadastroit.services.web.mapper.PessoaMapper;
import br.com.cadastroit.services.web.mapper.TarefaMapper;
import br.com.cadastroit.services.web.mapper.TelefoneMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ApiCadastroCommonsController {

	@Autowired
	public EntityManagerFactory entityManagerFactory;
	
	@Autowired
	protected final ObjectMapper mapperJson;
	
	protected final String EMPTY_MSG = "List is empty...";

	protected final PessoaRepository pessoaRepository;
	protected final EmpresaRepository empresaRepository;
	protected final TarefaRepository tarefaRepository;
	protected final TarefaService tarefaService;
	protected final DepartamentoRepository departamentoRepository;
	protected final EnderecoRepository enderecoRepository;
	protected final TelefoneRepository telefoneRepository;
	protected final ParametroRepository parametroRepository;
	protected final PessoaEmpresaRepository pessoaEmpresaRepository;
	protected final PessoaService pessoaService;
	protected final PedidoRepository pedidoRepository;
	protected final PedidoService pedidoService;
		
	protected final EmpresaMapper empresaMapper = Mappers.getMapper(EmpresaMapper.class);
	protected final EnderecoMapper enderecoMapper = Mappers.getMapper(EnderecoMapper.class);
	protected final PessoaMapper pessoaMapper = Mappers.getMapper(PessoaMapper.class);
	protected final TarefaMapper tarefaMapper = Mappers.getMapper(TarefaMapper.class);
	protected final DepartamentoMapper departamentoMapper = Mappers.getMapper(DepartamentoMapper.class);
	protected final TelefoneMapper telefoneMapper = Mappers.getMapper(TelefoneMapper.class);
	protected final ParametroMapper parametroMapper = Mappers.getMapper(ParametroMapper.class);
	protected final PessoaEmpresaMapper pessoaEmpresaMapper = Mappers.getMapper(PessoaEmpresaMapper.class);
	protected final PedidoMapper pedidoMapper = Mappers.getMapper(PedidoMapper.class);


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

	protected <T, R> List<R> mountObject(Function<T, R> mapper, List<T> list) {
		return list.stream().map(mapper).collect(Collectors.toList());
	}

	public boolean validateStatusTarefa(BigDecimal status) {
		return new HashSet<>(Arrays.asList(1L)).contains(status.longValue());
	}
	
	public HashMap<Boolean, String> validarCredencial(String token, Integer filtrar, FiltersDTO filterDto)
			throws Exception {

		EntityManager entityManager = this.entityManagerFactory.createEntityManager();
		
		HashMap<Boolean, String> result = new HashMap<>();
		
		try {
			
            String[] credencial = new String(Base64.getDecoder().decode(token)).split("&");
			result.put(false, "Empresa[ID] = " + filterDto.getEmpresaId() + " inativa...");
			return result;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		} finally {
			if (entityManager.isOpen()) {
				entityManager.clear();
				entityManager.close();
			}
		}
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
	
	protected HttpHeaders httpHeaders(String count) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("summaryCount", count);
		return headers;
	}
	
	protected HttpHeaders httpHeaders(String count, Long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("summaryCount", count);
		headers.add("pessoaId", String.valueOf(id));
		return headers;
	}
}
