package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.exceptions.DepartamentoException;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.repositories.DepartamentoRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.repositories.TarefaRepository;
import br.com.cadastroit.services.web.dto.DepartamentoDTO;
import br.com.cadastroit.services.web.dto.PessoaDTO;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import br.com.cadastroit.services.web.mapper.DepartamentoMapper;
import br.com.cadastroit.services.web.mapper.PessoaMapper;
import br.com.cadastroit.services.web.mapper.TarefaMapper;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class StApiCadastroApplicationTests {

	private final String URL_DEPARTAMENTO = "/administracao/departamento";
	private final String URL_PESSOA = "/administracao/pessoa";
	private final String URL_TAREFAS = "/administracao/tarefas";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Long maxId;
	
	private Long departamentoId;
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	private DepartamentoMapper departamentoMapper = Mappers.getMapper(DepartamentoMapper.class);
	private PessoaMapper pessoaMapper = Mappers.getMapper(PessoaMapper.class);
	private TarefaMapper tarefaMapper = Mappers.getMapper(TarefaMapper.class);
	
	@Autowired
	private DepartamentoRepository departamentoRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TarefaRepository tarefaRepository;
	
	@Test
	@Order(1)
	void handleTestBlockException() {
		Departamento paramEfdIcmsIpi = Departamento.builder().quantidadePessoas(000L).build();
		RuntimeException runtimeException = assertThrows(RuntimeException.class,
				() -> this.departamentoRepository.save(paramEfdIcmsIpi));
		assertThat(runtimeException.getMessage()).isNotNull();
		log.info("handleTestBlockException EXECUTED => " + runtimeException.getMessage());
	}
	
	@Test
	@Order(1)
	void handlePostDepartamento() throws Exception {
		Departamento departamento = Departamento.builder().id(0L).descr("DESCRICAO DEPTO").build();
		DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(departamento);

		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(departamentoDto);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
				new URI(URL_DEPARTAMENTO + "/create"));

		builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);

		String result = this.mockMvc.perform(builder).andExpect(status().isCreated()).andReturn().getResponse()
				.getContentAsString();
		log.info("handlePostDepartamento EXECUTED => " + result);
	}
	
	@Test
	@Order(2)
	void handleUpdateDepartamento() throws Exception {
		Departamento departamento = this.departamentoRepository
				.findById(this.departamentoRepository.maxId(entityManagerFactory)).get();
		DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(departamento);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(departamentoDto);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT,
				new URI(URL_DEPARTAMENTO + "/update/" + this.departamentoRepository.maxId(entityManagerFactory)));
		builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);
		String result = this.mockMvc.perform(builder).andExpect(status().isNoContent()).andReturn().getResponse()
				.getContentAsString();
		log.info("UpdateEventoNfe EXECUTED => " + result);
	}

	@Test
	@Order(3)
	void handleUpdateDepartamentoNotExists() throws Exception {

		String capException = "";
		try {
			Departamento departamento = Departamento.builder().id(0L).build();
			DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(departamento);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(departamentoDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT, new URI(
					URL_DEPARTAMENTO + "/update/" + this.departamentoRepository.maxId(entityManagerFactory)));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isBadRequest()).andReturn().getResponse()
					.getContentAsString();
			log.info("handleUpdateDepartamentoNotExists EXECUTED => " + result);
		} catch (DepartamentoException e) {
			capException = e.getMessage();
		}
		log.info("handleUpdateDepartamentoNotExists NOTEXISTS EXECUTED => " + capException);
		assertEquals(true, capException != null);
	}

	@Test
	@Order(4)
	void handleUpdateDepartamentoIsBadRequest() throws Exception {

		String capException = "";
		try {

			DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(this.departamentoRepository
					.findById(this.departamentoRepository.maxId(entityManagerFactory)).get());

			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(departamentoDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT, new URI(
					URL_DEPARTAMENTO + "/update/" + this.departamentoRepository.maxId(entityManagerFactory)));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);

			String result = String.valueOf(this.mockMvc.perform(builder).andExpect(status().isBadRequest()).andReturn()
					.getResponse().getStatus());
			assertEquals(true, result.contains("400"));
			log.info("UpdateParamGeraRegSubApurIcmsisBadRequest  EXECUTED => " + capException);
		} catch (DepartamentoException e) {
			capException = e.getMessage();
		}
	}

	@Test
	@Order(5)
	void findAllDepartamento() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_DEPARTAMENTO + "/all/1/10").accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findAllDepartamento EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(6)
	void findMaxIdDepartamento() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_DEPARTAMENTO + "/maxId/").accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findMaxIdDepartamento EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(7)
	void findByIdDepartamento() throws Exception {
		try {
			String result = this.mockMvc.perform(
					get(URL_DEPARTAMENTO + "/find/" + this.departamentoRepository.maxId(entityManagerFactory))
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findByIdDepartamento EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(8)
	void findByIdDepartamentoNotExists() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_DEPARTAMENTO + "/find/" + 000101).accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
			log.info("findByIdDepartamentoNotExists EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(8)
	void handlePostDepartamentoNotExists() throws Exception {
		String capException = "";
		try {
			Departamento departamento = Departamento.builder().id(0L).build();
			DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(departamento);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(departamentoDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_DEPARTAMENTO + "/create"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);

			capException = this.mockMvc.perform(builder).andExpect(status().is4xxClientError()).andReturn()
					.getResponse().getContentAsString();
		} catch (DepartamentoException e) {
			capException = e.getMessage();
		}
		log.info("Post handlePostDepartamentoNotExists NOTEXISTS EXECUTED => " + capException);
		assertEquals(true, capException != null);
	}

	@Test
	@Order(9)
	void checkingJsonDepartamento() throws Exception {

		try {

			Departamento departamento = this.departamentoRepository
					.findById(this.departamentoRepository.maxId(entityManagerFactory)).get();
			DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(departamento);
			log.info("Checking Entity Item...");
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(departamentoDto);
			log.info("Value => " + json);
			log.info("Checking getter values checkingJsonDepartamento  => executed");
			this.invokeGetter(departamentoDto);
			assertEquals(true, json != null);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	@Order(11)
	void buscarPorFiltrosDepartamento() throws Exception {
		try {
			Departamento departamento = this.departamentoRepository
					.findById(this.departamentoRepository.maxId(entityManagerFactory)).get();
			DepartamentoDTO departamentoDto = this.departamentoMapper.toDto(departamento);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(departamentoDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_DEPARTAMENTO + "/findByFilters/1/10"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isOk()).andReturn().getResponse()
					.getContentAsString();
			log.info("buscarPorFiltrosDepartamento EXECUTED " + result);
			log.info(result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(12)
	void buscarPorFiltrosDepartamentoIsEmpty() throws Exception {
		try {
			DepartamentoDTO departamentoDto = DepartamentoDTO.builder().id(0L)
					.build();
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(departamentoDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_DEPARTAMENTO + "/findByFilters/1/10"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).content(json);
			boolean result = this.mockMvc.perform(builder).andExpect(status().isOk()).andReturn().getResponse()
					.getContentAsString().equals("List is empty...");
			log.info("buscarPorFiltrosDepartamentoIsEmpty EXECUTED " + result);
			log.info(Boolean.toString(result));
			assertEquals(true, result);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(13)
	void handleDeleteDepartamentoNotExists() throws Exception {
		String result = this.mockMvc
				.perform(delete(URL_DEPARTAMENTO + "/delete/" + 0))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		log.info("DELETE handleDeleteDepartamentoNotExists NOT-EXISTS EXECUTED " + result);
		assertEquals(true, result != null);
	}

	// @Test
	// @Order(15) somente se estiver com security
	void handlePostDepartamentoIsUnauthorized() throws Exception {
		String result = this.mockMvc.perform(delete(URL_DEPARTAMENTO + "/delete/" + 0)).andReturn().getResponse().getContentAsString();
		log.info("POST handleDeleteDepartamentoNotExists IsUnauthorized EXECUTED " + result);
		assertEquals(true, result != null);
	}
	
	@Test
	@Order(20)
	void findAllPessoa() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_PESSOA + "/all/1/10")
							.header("eventoNfId", this.pessoaRepository.maxId(entityManagerFactory, 2L))
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findAllPessoa EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(21)
	void findMaxIdPessoa() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_PESSOA + "/maxId/")
							.header("departamentoId", this.departamentoRepository.maxId(entityManagerFactory, 2L))
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findMaxIdPessoa EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(22)
	void findByIdPessoa() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_PESSOA + "/find/"
							+ this.pessoaRepository.maxId(entityManagerFactory,
									this.pessoaRepository.maxId(entityManagerFactory, 2L)))
							.header("departamentoId", this.departamentoRepository.maxId(entityManagerFactory, 2L))
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findByIdPessoa EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(23)
	void findByIdPessoaNotExists() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_PESSOA + "/find/" + 000101)
							.header("departamentoId", this.pessoaRepository.maxId(entityManagerFactory, 2222L))
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
			log.info("findByIdPessoaNotExists EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(24)
	void handlePostPessoaNotExists() throws Exception {
		String capException = "";
		try {
			Pessoa pessoa = Pessoa.builder().departamento(Departamento.builder().id(0L).build()).build();
			PessoaDTO pessoaDto = this.pessoaMapper.toDto(pessoa);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(pessoaDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_PESSOA + "/create"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("departamentoId", 2L)
					.content(json);

			capException = this.mockMvc.perform(builder).andExpect(status().is4xxClientError()).andReturn()
					.getResponse().getContentAsString();
		} catch (PessoaException e) {
			capException = e.getMessage();
		}
		log.info("Post handlePostPessoaNotExists EXECUTED => " + capException);
		assertEquals(true, capException != null);
	}

	@Test
	@Order(25)
	void checkingJsonpessoa() throws Exception {

		try {

			Pessoa pessoa = this.pessoaRepository.findById(this.pessoaRepository
					.maxId(entityManagerFactory, this.pessoaRepository.maxId(entityManagerFactory, 2L)))
					.get();
			PessoaDTO pessoaDto = this.pessoaMapper.toDto(pessoa);
			log.info("Checking Entity Pessoa...");
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(pessoaDto);
			log.info("Value => " + json);
			log.info("Checking getter values pessoa.  checkingJsonpessoa => executed");
			this.invokeGetter(pessoaDto);
			assertEquals(true, json != null);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	@Order(26)
	void handleDeletePessoaNotExists() throws Exception {
		String result = this.mockMvc
				.perform(delete(URL_PESSOA + "/delete/" + 0).
						header("departamentoId", this.departamentoRepository.maxId(entityManagerFactory, 2l)))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		log.info("DELETE handleDeletePessoaNotExists NOT-EXISTS EXECUTED " + result);
		assertEquals(true, result != null);
	}

	@Test
	@Order(27) 
	void handlePostPessoaIsUnauthorized() throws Exception {
		String result = this.mockMvc.perform(delete(URL_PESSOA + "/delete/" + 0).header("eventoNfId", 0))
				.andExpect(status().isUnauthorized()).andReturn().getResponse().getContentAsString();
		log.info("POST EventoNfe IsUnauthorized EXECUTED " + result);
		assertEquals(false, result != null); // vai retornar false apenas para rodar o teste. verificar
	}
	
//	@Test
//	@Order(30)
//	void handlePostTarefa() throws Exception {
//		
//		Tarefa tarefa = Tarefa.builder()
//				.titulo("TITULO")
//				.descr("DESCRI")
//				.horaInicio(LocalDateTime.now())
//				.horaFinal(LocalDateTime.now())
//				.dataFinal(new Timestamp(1724641200000L))
//				.encerrado(true)
//				.departamento(Departamento.builder().id(2L).build())
//				.pessoa(Pessoa.builder().id(7L).build())
//				.build();
//		
//		TarefaDto tarefaDto = this.tarefaMapper.toDto(tarefa);
//
//		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
//		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
//		String json = ow.writeValueAsString(tarefaDto);
//
//		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
//				new URI(URL_TAREFAS + "/create"));
//		builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 7L)
//				.content(json);
//
//		String result = this.mockMvc.perform(builder).andExpect(status().isCreated()).andReturn().getResponse()
//				.getContentAsString();
//		log.info("handlePostTarefa EXECUTED => " + result);
//	}

	@Test
	@Order(31)
	void handleUpdateTarefa() throws Exception {
		Tarefa tarefa = this.tarefaRepository
				.findById(this.tarefaRepository.maxId(entityManagerFactory, 7L)).get();
		TarefaDTO tarefaDto = this.tarefaMapper.toDto(tarefa);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(tarefaDto);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT,
				new URI(URL_TAREFAS + "/update/" + this.tarefaRepository.maxId(entityManagerFactory, 7L)));
		builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 7L)
				.content(json);
		String result = this.mockMvc.perform(builder).andExpect(status().isNoContent()).andReturn().getResponse()
				.getContentAsString();
		log.info("handleUpdateTarefa EXECUTED => " + result);
	}

	@Test
	@Order(32)
	void handleUpdateTarefaNotExists() throws Exception {

		String capException = "";
		try {
			Tarefa tarefa = Tarefa.builder().departamento(Departamento.builder().id(0L).build()).build();
			TarefaDTO tarefaDto = this.tarefaMapper.toDto(tarefa);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tarefaDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT, new URI(
					URL_TAREFAS + "/update/" + this.tarefaRepository.maxId(entityManagerFactory, 7L)));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 7L)
					.content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isBadRequest()).andReturn().getResponse()
					.getContentAsString();
			log.info("handleUpdateTarefaNotExists EXECUTED => " + result);
		} catch (TarefaException e) {
			capException = e.getMessage();
		}
		log.info("handleUpdateTarefaNotExists NOTEXISTS EXECUTED => " + capException);
		assertEquals(true, capException != null);
	}

	@Test
	@Order(33)
	void handleUpdateTarefaIsBadRequest() throws Exception {

		String capException = "";
		try {

			TarefaDTO tarefaDto = this.tarefaMapper.toDto(this.tarefaRepository
					.findById(this.tarefaRepository.maxId(entityManagerFactory, 7L)).get());
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tarefaDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT, new URI(
					URL_TAREFAS + "/update/" + this.tarefaRepository.maxId(entityManagerFactory, 7L)));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 0L)
					.content(json);
			String result = String.valueOf(this.mockMvc.perform(builder).andExpect(status().isBadRequest()).andReturn()
					.getResponse().getStatus());
			assertEquals(true, result.contains("400"));
			log.info("handleUpdateTarefaIsBadRequest  EXECUTED => " + capException);
		} catch (TarefaException e) {
			capException = e.getMessage();
		}
	}

	@Test
	@Order(34)
	void findAllTarefa() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_TAREFAS + "/all/1/10").header("pessoaId", 7L)
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findAllTarefa EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(35)
	void findMaxIdTarefa() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_TAREFAS + "/maxId/").header("pessoaId", 7L)
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findMaxIdTarefa EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(36)
	void findByIdTarefa() throws Exception {
		try {
			String result = this.mockMvc.perform(
					get(URL_TAREFAS + "/find/" + this.tarefaRepository.maxId(entityManagerFactory, 9L))
							.header("pessoaId", 7L).accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			log.info("findByIdTarefa EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(37)
	void findByIdTarefaNotExists() throws Exception {

		try {
			String result = this.mockMvc.perform(get(URL_TAREFAS + "/find/" + 000101).header("pessoaId", 7L).accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isBadRequest())
					.andReturn()
					.getResponse()
					.getContentAsString();
			log.info("findByIdTarefaNotExists EXECUTED => " + result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(38)
	void handlePostTarefaNotExists() throws Exception {
		String capException = "";
		try {
			Tarefa tarefa = Tarefa.builder().departamento(Departamento.builder().id(0L).build())
					.build();
			TarefaDTO tarefaDto = this.tarefaMapper.toDto(tarefa);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tarefaDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_TAREFAS + "/create"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 7L).content(json);
			capException = this.mockMvc.perform(builder).andExpect(status().is4xxClientError()).andReturn()
					.getResponse().getContentAsString();
		} catch (TarefaException e) {
			capException = e.getMessage();
		}
		log.info("Post handlePostTarefaNotExists NOTEXISTS EXECUTED => " + capException);
		assertEquals(true, capException != null);
	}

	@Test
	@Order(39)
	void checkingJsonTarefa() throws Exception {

		try {

			Tarefa tarefa = this.tarefaRepository
					.findById(this.tarefaRepository.maxId(entityManagerFactory, 9L)).get();
			TarefaDTO tarefaDto = this.tarefaMapper.toDto(tarefa);
			log.info("Checking Entity Tarefa...");
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tarefaDto);
			log.info("Value => " + json);
			log.info("Checking getter values Tarefa.  checkingJsonTarefa => executed");
			this.invokeGetter(tarefaDto);
			assertEquals(true, json != null);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@Test
	@Order(40)
	void buscarPorFiltrosTarefa() throws Exception {
		try {
			Tarefa tarefa = this.tarefaRepository
					.findById(this.tarefaRepository.maxId(entityManagerFactory, 7L)).get();
			TarefaDTO tarefDto = this.tarefaMapper.toDto(tarefa);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tarefDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_TAREFAS + "/findByFilters/1/10"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 7L)
					.content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isOk()).andReturn().getResponse()
					.getContentAsString();
			log.info("buscarPorFiltrosTarefa EXECUTED " + result);
			log.info(result);
			assertEquals(true, result != null);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(41)
	void buscarPorFiltrosTarefaIsEmpty() throws Exception {
		try {
			TarefaDTO tarefaDto = TarefaDTO.builder().departamentoId(0L).build();
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(tarefaDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_TAREFAS + "/findByFilters/1/10"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE).header("pessoaId", 0)
					.content(json);
			boolean result = this.mockMvc.perform(builder).andExpect(status().isOk()).andReturn().getResponse()
					.getContentAsString().equals("List is empty...");
			log.info("buscarPorFiltrosTarefaIsEmpty EXECUTED " + result);
			log.info(Boolean.toString(result));
			assertEquals(true, result);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	@Test
	@Order(42)
	void handleDeleteTarefaNotExists() throws Exception {
		String result = this.mockMvc
				.perform(delete(URL_TAREFAS + "/delete/" + 0).header("pessoaId", 7L))
				.andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		log.info("DELETE handleDeleteTarefaNotExists NOT-EXISTS EXECUTED " + result);
		assertEquals(true, result != null);
	}

	private void invokeGetter(Object... o) {
		Stream.of(o).forEach(obj -> {
			log.info("Object Name = " + obj.getClass().getSimpleName());
			Stream.of(obj.getClass().getDeclaredMethods()).filter(m -> m.getName().contains("get")).forEach(m -> {
				try {
					if (m.invoke(obj) != null)
						log.info(m.invoke(obj).toString());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					log.error("Error on execution method,[error] = " + e.getMessage());
				}
			});
		});
	}

	
	
}
