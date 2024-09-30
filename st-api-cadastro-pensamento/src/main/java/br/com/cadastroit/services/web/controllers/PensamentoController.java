package br.com.cadastroit.services.web.controllers;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.api.domain.Pensamento;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.exceptions.PensamentoException;
import br.com.cadastroit.services.repositories.PensamentoRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.web.controllers.commons.ApiCadastroCommonsController;
import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
import br.com.cadastroit.services.web.dto.PensamentoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/pensamento")
public class PensamentoController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private final Logger logger = Logger.getLogger(PensamentoController.class);

	@Operation(summary = "Get Max Id from Thoughts")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Thoughts  created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PensamentoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Get a Thoughts max id record")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("pessoaId") Long pessoaId) {

		try {
			Long id = pensamentoRepository.maxId(entityManagerFactory, pessoaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Get a Thoughts by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Thoughts", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Pensamento.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Tarefa not found", content = @Content) })
	@Tag(name = "Thoughts", description = "Get a Thoughts record by id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pensamento entity = pensamentoRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(pensamentoMapper.toDTO(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Create Thoughts")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Thoughts created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PensamentoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Thoughts", description = "Create a Thoughts record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader(value="pessoaId", required = false) Long pessoaId, @Validated @RequestBody PensamentoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			return mountEntity(dto, pessoaId, null, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Update Thoughts")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order updated with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Pensamento.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Thoughts", description = "Updating a Thoughts record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader(value="pessoaId", required = false) Long pessoaId, @PathVariable("id") Long id, @Validated @RequestBody PensamentoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			return this.mountEntity(dto, id, null, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Delete Thoughts By id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "No Content", description = "Update Thoughts", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Thoughts", description = "Deleting a Thoughts record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Long id) throws PensamentoException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			pensamentoRepository.delete(pensamentoRepository.findById(id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Search all Thoughts")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Thoughts", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pensamento.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Thoughts", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader(value="pessoaId", required = false) Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = pensamentoRepository.count(pessoaId, null, entityManagerFactory);
			if (count > 0) {
				List<Pensamento> pensamentos = pensamentoRepository.findAll(entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pensamentoMapper.toDTO(pensamentos));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Search all Thoughts by filters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Thoughts", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pensamento.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Thoughts", description = "Get Order by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader(value="pessoaId", required = false) Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody PensamentoDTO dto) {

		try {
			Long count = pensamentoRepository.count(pessoaId, dto, entityManagerFactory);
			if (count > 0) {
				
				List<Pensamento> list = pensamentoRepository.findByFilters(pessoaId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pensamentoMapper.toDTO(list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}


//	public void insertDocumentMongo(AwsBucketUploadModel model) throws PensamentoException {
//
//		try {
//
//			GsonBuilder builder = new GsonBuilder();
//			builder.setLenient();
//			Gson gSonDesif = builder.create();
//			String modelRetornado = objectToJson(model);
//			CollectionRelatorioApi requestObject = gSonDesif.fromJson(objectToJson(model), CollectionRelatorioApi.class);
//			requestObject.setID(model.getUuid().toString());
//			requestObject.setSTATUS(model.getCode());
//			requestObject.setPESSOA_ID(model.getPessoaId());
//			requestObject.setCREATIONDATE(UtilDate.toDateTimeStringUTC(Timestamp.from(Instant.now())));
//			requestObject.setTYPEARCHIVE("JSON");
//			requestObject.setNROPROTOCOLO(model.getNroProtocolo());
//			Map<String, Object> map = requestObject.toMap(requestObject);
//			this.collectionRelatorioApiRepository.insertDocument("relatoriocontasdata", map);
//
//		} catch (Exception ex) {
//			throw new PensamentoException(String.format("ERROR ON INSERT DATA IN MONGO DATABASE " + ex.getMessage()));
//		}
//	}

	private ResponseEntity<Object> mountEntity(PensamentoDTO dto, Long id, Pessoa pessoa, boolean update) throws PensamentoException {

		try {

			Pensamento entity = pensamentoMapper.toEntity(dto);
			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity = pensamentoRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new PensamentoException(ex.getMessage(), ex);
		}
	}
	
	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.pessoaRepository(new PessoaRepository())
				.pensamentoRepository(new PensamentoRepository())
				.build();
	}

	public PensamentoController(ObjectMapper mapperJson, PessoaRepository pessoaRepository,
			PensamentoRepository pensamentoRepository) {

		super(mapperJson, pessoaRepository, pensamentoRepository);
	}

}
