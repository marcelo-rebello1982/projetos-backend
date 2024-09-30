package br.com.cadastroit.services.web.controllers;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

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

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.exceptions.PessoaException;
import br.com.cadastroit.services.repositories.DepartamentoRepository;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.EnderecoRepository;
import br.com.cadastroit.services.repositories.ParametroRepository;
import br.com.cadastroit.services.repositories.PedidoRepository;
import br.com.cadastroit.services.repositories.PessoaEmpresaRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.repositories.TarefaRepository;
import br.com.cadastroit.services.repositories.TelefoneRepository;
import br.com.cadastroit.services.web.controllers.commons.ApiCadastroCommonsController;
import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
import br.com.cadastroit.services.web.dto.EmpresaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/empresa")
public class EmpresaController extends ApiCadastroCommonsController {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Tag(name = "Empresa", description = "Get maxId From Empresa")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId() {

		try {
			Long id = empresaRepository.maxId(entityManagerFactory);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Tag(name = "Empresa", description = "Get Empresa by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Empresa entity = empresaRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(mountObject(empresaMapper::toDto, entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@Operation(summary = "Search all Empresa")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Departaments", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Departamento.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departamento", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = empresaRepository.count(null, entityManagerFactory);
			if (count > 0) {
				List<Departamento> list = departamentoRepository.findAll(entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(mountObject(departamentoMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Create Empresa")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Item created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = EmpresaDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Empresa", description = "Create a Empresa record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@Validated @RequestBody EmpresaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			return mountEntity(dto, null, null, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Empresa", description = "Updating a Empresa record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id, @Validated @RequestBody EmpresaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			return this.mountEntity(dto, id, null, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Empresa", description = "Deleting a Empresa record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) throws PessoaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			pessoaRepository.delete(pessoaRepository.findById(empresaId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	private ResponseEntity<Object> mountEntity(EmpresaDTO dto, Long id, Empresa empresa, boolean update) throws PessoaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Empresa entity = empresaMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity = empresaRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new PessoaException(ex.getMessage(), ex);
		}
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.pessoaRepository(new PessoaRepository())
				.empresaRepository(new EmpresaRepository())
				.build();
	}

	public EmpresaController(ObjectMapper mapperJson,  PessoaRepository pessoaRepository, EmpresaRepository empresaRepository, TarefaRepository tarefaRepository, TarefaService tarefaService, DepartamentoRepository departamentoRepository, EnderecoRepository enderecoRepository,  TelefoneRepository telefoneRepository, ParametroRepository parametroRepository , PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {
		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository, telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
