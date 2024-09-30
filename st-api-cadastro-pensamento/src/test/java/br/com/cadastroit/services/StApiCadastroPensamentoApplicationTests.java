package br.com.cadastroit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Timestamp;
import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
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

import br.com.cadastroit.services.api.domain.Pensamento;
import br.com.cadastroit.services.exceptions.PensamentoException;
import br.com.cadastroit.services.repositories.PensamentoRepository;
import br.com.cadastroit.services.web.dto.PensamentoDTO;
import br.com.cadastroit.services.web.mapper.PensamentoMapper;
import lombok.extern.slf4j.Slf4j;


@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
class StApiCadastroPensamentoApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	private String pensamentoMaxResult;

	private Long pensamentoMaxId;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private PensamentoRepository pensamentoRepository;
	
	@Autowired
	private PensamentoMapper pensamentoMapper;

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	private final String passwordAuth = "Basic Y3N0LWFkbWluLTIweHgjMTokMnkkMTIkdnVTUk1CNFRmOHpMdVhvLkdmeDVXZU5QMGttWWxRNXpOUzFPMHJJb3hSLmgzQ3lNUnN3b2k=";
	
	private final String URL_PRINCIPAL = "/administracao/pensamento";
	
	@BeforeEach
	public void beforeEach() throws Exception {

		pensamentoMaxResult = this.mockMvc
				.perform(get(URL_PRINCIPAL + "/maxId").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn().getResponse().getContentAsString();
		this.pensamentoMaxId = (pensamentoMaxResult.equals("") || pensamentoMaxResult == null
				? Long.valueOf(0L)
				: Long.valueOf(pensamentoMaxResult));

	}
	
	// aqui poderia vir um token de autorização para permitir requests somente de autorizados,
	// junto ao spring security 
	//
	// public void beforeEach1() throws Exception {
    //
	//   pensamentoMaxResult = this.mockMvc
	//			.perform(get(URL_PENSAMENTOS + "/maxId").header("empresaId", empresaId)
	//					.header("Authorization", passwordAuth).accept(MediaType.APPLICATION_JSON_VALUE))
	//			.andReturn().getResponse().getContentAsString();
	//	this.pensamentoMaxId = (pensamentoMaxResult.equals("") || pensamentoMaxResult == null
	//			? new Long(0L)
	//			: new Long(pensamentoMaxResult));
    //
	// }
	
	@Test
	@Order(1)
	void handlePostPensamentoAllFieldsNullAndReturnBlockException() {
		Pensamento entity = Pensamento.builder()
				.autoria(null)
				.conteudo(null)
				.modelo(null)
				.build();
		
		RuntimeException runtimeException = assertThrows(RuntimeException.class,
				() -> pensamentoRepository.save(entity));
		assertThat(runtimeException.getMessage()).isNotNull();
		log.info("handlePostPensamentoAllFieldsNullAndReturnBlockException EXECUTED => " + runtimeException.getMessage());
	}
	
	@Test
	@Order(2)
	void handlePostPensamento() throws Exception {

		Pensamento entity = Pensamento.builder()
				.conteudo(RandomStringUtils.secure().next(255, true, true))
				.autoria(RandomStringUtils.secure().next(50, true, true))
				.build();

		PensamentoDTO entityDto = this.pensamentoMapper.toDTO(entity);

		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(entityDto);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
				new URI(URL_PRINCIPAL + "/create"));
		builder.contentType(MediaType.APPLICATION_JSON_VALUE)
			//	.header("pessoaId", pessoaId)
			//	.header("Authorization", passwordAuth)
			.content(json);
		String result = this.mockMvc.perform(builder).andExpect(status().isCreated())
				.andExpect(s -> assertEquals(201, s.getResponse().getStatus())).andReturn().getResponse()
				.getContentAsString();
		log.info("handlePostPensamento EXECUTED => " + result);
	}
	
	@Test
	@Order(3) // aqui apenas testa as validações do Bean Validation @Size
	void handlePostPensamentoAndReturnMaxSizeErrorAnotation() throws Exception {

		PensamentoDTO entityDto = PensamentoDTO.builder()
				.conteudo(RandomStringUtils.secure().next(1000, true, true))
				.autoria(RandomStringUtils.secure().next(1000, true, true))
				.build();

		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(entityDto);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
				new URI(URL_PRINCIPAL + "/create"));
		builder.contentType(MediaType.APPLICATION_JSON_VALUE)
				//.header("pessoaId", pessoaId)
				// .header("Authorization", passwordAuth)
				.content(json);
		String result = this.mockMvc.perform(builder).andExpect(status().isBadRequest())
				.andExpect(s -> assertEquals(true, s.getResponse().getContentAsString().contains("Size"))).andReturn()
				.getResponse().getContentAsString();
		log.info("handlePostPensamentoAndReturnMaxSizeErrorAnotation EXECUTED => " + result);
	}
	
	@Test
	@Order(4)
	void handlePostPensamentoAndReturnBadRequest() throws Exception {

		String capException = "";
		try {

			PensamentoDTO entityDto = this.pensamentoMapper
					.toDTO(this.pensamentoRepository.findById(pensamentoMaxId).get());

			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(entityDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_PRINCIPAL + "/create"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE)
					//.header("pessoaId", 0L) 
					//.header("Authorization", passwordAuth)
					.content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isBadRequest())
					.andExpect(s -> assertEquals(true, s.getResponse().getContentAsString().contains("No entity")))
					.andReturn().getResponse().getContentAsString();
			log.info("handlePostPensamentoAndReturnBadRequest  EXECUTED => " + result);
		} catch (PensamentoException e) {
			capException = e.getMessage();
		}
	}
	
	@Test
	@Order(5)
	void findByIdPensamento() throws Exception {

		String capException = "";
		try {
			String result = this.mockMvc
					.perform(get(URL_PRINCIPAL + "/find/" + pensamentoMaxId)
							//.header("pessoaId", pessoaId)
							//.header("Authorization", passwordAuth)
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(r -> assertNotNull(r.getResponse().getContentAsString())).andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();
			log.info("findByIdPensamento EXECUTED => " + result);
		} catch (PensamentoException e) {
			capException = e.getMessage();
		}
	}
	
	@Test
	@Order(6)
	void findByIdPensamentoWithNotExistsAndReturnBadRequest() throws Exception {

		String capException = "";
		try {

			int result = this.mockMvc
					.perform(get(URL_PRINCIPAL + "/find/" + pensamentoMaxId + 1)
							//.header("pessoaId", pessoaId)
							//.header("Authorization", passwordAuth)
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isBadRequest()).andExpect(s -> assertEquals(400, s.getResponse().getStatus()))
					.andReturn().getResponse().getStatus();
			log.info("findByIdPensamentoWithNotExistsAndReturnBadRequest  EXECUTED => " + result);
		} catch (Exception e) {
			capException = e.getMessage();
		}
	}
	
	@Test
	@Order(7)
	void findMaxIdPensamento() throws Exception {
		try {
			String result = this.mockMvc
					.perform(
							get(URL_PRINCIPAL + "/maxId/")
							//.header("pessoaId", pessoaId)
							//.header("Authorization", passwordAuth)
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(r -> assertNotNull(r.getResponse().getContentAsString())).andExpect(status().isOk())
					.andReturn().getResponse().getContentAsString();
			log.info("findMaxIdPensamento EXECUTED => " + result);
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	@Test
	@Order(8)
	void handleUpdatePensamento() throws Exception {
		Pensamento entity = this.pensamentoRepository.findById(pensamentoMaxId).get();
		PensamentoDTO entityDto = this.pensamentoMapper.toDTO(entity);
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(entityDto);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT,
				new URI(URL_PRINCIPAL + "/update/" + pensamentoMaxId));
		builder.contentType(MediaType.APPLICATION_JSON_VALUE)
				//.header("pessoaId", pessoaId)
				//.header("Authorization", passwordAuth)
				.content(json);
		String result = this.mockMvc.perform(builder).andExpect(status().isNoContent())
				.andExpect(r -> assertNotNull(r.getResponse().getContentAsString())).andReturn().getResponse()
				.getContentAsString();
		log.info("handleUpdatePensamento EXECUTED => " + result);

	}
	
	@Test
	@Order(9) // aqui apenas testa as validações do Bean Validation @NotNull
	void handleUpdatePensamentoAntAndReturnErrorFieldsWithAnnotationNotNull() throws Exception {

		try {
			PensamentoDTO entityDto = PensamentoDTO.builder().id(0L).build();
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(entityDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.PUT,
					new URI(URL_PRINCIPAL + "/update/" + pensamentoMaxId));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE)
				//	.header("pessoaId", pessoaId)
				//	.header("Authorization", passwordAuth)
					.content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isBadRequest())
					.andExpect(r -> assertTrue(r.getResponse().getContentAsString().contains("NotNull"))).andReturn()
					.getResponse().getContentAsString();
			log.info("handleUpdatePensamentoAntAndReturnErrorFieldsWithAnnotationNotNull  EXECUTED => " + result);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(10)
	void findAllPensamento() throws Exception {
		try {
			String result = this.mockMvc
					.perform(get(URL_PRINCIPAL + "/all/1/10?order=desc")
						//	.header("empresaId", empresaId)
						//	.header("Authorization", passwordAuth)
							.accept(MediaType.APPLICATION_JSON_VALUE))
					.andExpect(status().isOk()).andExpect(s -> assertEquals(200, s.getResponse().getStatus()))
					.andReturn().getResponse().getContentAsString();
			log.info("findAllPensamento EXECUTED => " + result);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(11)
	void findByFilters() throws Exception {
		try {
			
			// neste teste a idéia é simplemente testar os endpoint findByFilters ,
			// então recupera-se o ultimo objeto inserido no banco, converte para um
			// DTO e faz a busca no filtro passando  o mesmo objeto recebido.
			
			Pensamento entity = this.pensamentoRepository.findById(pensamentoMaxId).get();
			PensamentoDTO entityDto = this.pensamentoMapper.toDTO(entity);
			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(entityDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_PRINCIPAL + "/findByFilters/1/10"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE)
					// .header("pessoaId", pessoaId)
					// .header("Authorization", passwordAuth)
					.content(json);
			String result = this.mockMvc.perform(builder).andExpect(status().isOk())
					.andExpect(r -> assertNotNull(r.getResponse().getContentAsString())).andReturn().getResponse()
					.getContentAsString();
			assertEquals(true, result != null);
			log.info("findByFilters => executed " + result);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(12)
	void findByFiltersIsEmpty() throws Exception {
		
		try {
			
			// aqui a idéia é quando uma busca por um determinado filtro não retorna
			// nenhum objeto, é retornado apenas uma mensagem para o frontEnd que podera
			// trata-la de acordo com a necessidade.
			
			PensamentoDTO entityDto = PensamentoDTO.builder().conteudo("XXXYYYZZZ").build();

			mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(entityDto);
			MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(HttpMethod.POST,
					new URI(URL_PRINCIPAL + "/findByFilters/1/10"));
			builder.contentType(MediaType.APPLICATION_JSON_VALUE)
					// .header("pessoaId", pessoaId)
					// .header("Authorization", passwordAuth)
					.content(json);
			boolean result = this.mockMvc.perform(builder).andExpect(status().isOk())
					.andExpect(
							r -> assertEquals(true, r.getResponse().getContentAsString().contains("List is empty...")))
					.andReturn().getResponse().getContentAsString().equals("List is empty...");
			log.info("findByFiltersIsEmpty => executed " + result);
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}
	
	@Test
	@Order(13) // aqui apenas testa se por alguma eventualidade tentar apagar algum 
	           // registro que por algum erro ainda conste em tela mas não conste no banco 
	void handleDeletePensamentoNotFound() throws Exception {
		int result = this.mockMvc
				.perform(delete(URL_PRINCIPAL + "/delete/" + pensamentoMaxId + 1))
					//	.header("Authorization", passwordAuth)
					//	.header("pessoaId", pessoaId))
				.andExpect(status().isBadRequest())
				.andExpect(r -> r.getResponse().getContentAsString().contains("No entity"))
				.andExpect(s -> assertEquals(400, s.getResponse().getStatus())).andReturn().getResponse().getStatus();
		log.info("handleDeletePensamentoNotFound EXECUTED " + result);
	}
	
	@Test
	@Order(14) // aqui caso implementado uma camada de autenticação, apenas testa o erro de retorno. 
	void handleDeletePensamentoIsUnauthorized() throws Exception {
		int result = this.mockMvc
				.perform(delete(URL_PRINCIPAL + "/delete/" + pensamentoMaxId))
				// .header("pessoaId", pessoaId))
				.andExpect(status().isUnauthorized()).andExpect(r -> assertEquals(401, r.getResponse().getStatus()))
				.andReturn().getResponse().getStatus();
		log.info("handleDeletePensamentoIsUnauthorized => executed " + result);
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
