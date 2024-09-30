package br.com.cadastroit.services.web.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

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
import br.com.cadastroit.services.api.domain.HealthCheck;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.enums.ApiMessage;
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
import br.com.cadastroit.services.web.dto.PessoaDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/pessoa")
public class PessoaController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Tag(name = "Pessoa", description = "Get status from API - Consulta Pessoa")
	@GetMapping("/status")
	public ResponseEntity<Object> status(@RequestParam(name = "status", required = false, defaultValue = "UP") String status) {

		try {
			return new ResponseEntity<>(HealthCheck.builder().status(status).maxId(pessoaRepository.maxId(entityManagerFactory)).build(),
					HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Pessoa", description = "Get maxId Pessoa")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("departamentoId") Long departamentoId) {

		try {
			Long id = pessoaRepository.maxId(entityManagerFactory, departamentoId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}
	
	@Tag(name = "Pessoa", description = "Get Pessoa by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id, @RequestHeader(value = "token", required = false) String token) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = pessoaRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(pessoaMapper.toDto(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@Tag(name = "Pessoa", description = "Get Pessoa by Id")
	@GetMapping("/findByQueryParam/{id}/{deptoId}/{descr}")
	public ResponseEntity<Object> findByQueryParam(@PathVariable("id") Long id, @PathVariable("deptoId") Long deptoId, @PathVariable("descr") String  descr) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = pessoaRepository.findByQueryParam(id, deptoId, descr);
			return ResponseEntity.ok().body(pessoaMapper.toPessoaDto(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@GetMapping(value = "/arquivos-anexados/{id}")
	public void obterArquivosAnexados(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {

		byte[] bytes =  new byte[1] ; // this.pessoaService.obterArquivosAnexados(id);

		response.setContentType("application/download");
		response.setContentLength(bytes.length);
		response.setHeader("Content-Disposition", "attachment;");
		response.getOutputStream().write(bytes);
	}
	
	

	@Tag(name = "Pessoa", description = "Get Pessoa by Id")
	@GetMapping("/find/{departamentoId}/{id}")
	public ResponseEntity<Object> find(@RequestHeader("departamentoId") Long departamentoId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = pessoaRepository.findById(departamentoId, id, entityManagerFactory);
			return ResponseEntity.ok().body(pessoaMapper.toPessoaDto(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Pessoa", description = "Create a new Pessoa record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("departamentoId") Long departamentoId, @Validated @RequestBody PessoaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Departamento entity = commonsValidation.recuperarDepartamento(departamentoId, entityManagerFactory);
			return mountEntity(dto, null, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Pessoa", description = "Updating a Pessoa record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("departamentoId") Long departamentoId, @PathVariable("id") Long id, @Validated @RequestBody PessoaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Departamento entity = commonsValidation.recuperarDepartamento(departamentoId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Pessoa", description = "Deleting a Pessoa record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("departamentoId") Long departamentoId, @PathVariable("id") Long id) throws PessoaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			pessoaRepository.delete(pessoaRepository.findById(departamentoId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Pessoa", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("departamentoId") Long departamentoId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = pessoaRepository.count(departamentoId, null, entityManagerFactory);
			if (count > 0) {
				List<Pessoa> list = pessoaRepository.findAll(departamentoId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pessoaMapper.toPessoaResumedDto(list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Tag(name = "Pessoa", description = "Get Pessoa by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader(value ="departamentoId", required= false) Long departamentoId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody PessoaDTO dto) {

		try {
			
			Long count = pessoaRepository.count(departamentoId, dto, entityManagerFactory);
			
			if (count > 0) {
				List<Pessoa> pessoas = pessoaRepository.findByFilters(departamentoId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok()
						.headers(headers)
						.body(pessoaMapper.toPessoaTelefoneDto(pessoas));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(PessoaDTO dto, Long id, Departamento departamento, boolean update) throws PessoaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Pessoa entity = pessoaMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setDepartamento(departamento);
			entity.setId(update ? id : null);
			entity = pessoaRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return ResponseEntity.status(update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED)
					.headers(httpHeaders(null, entity.getId()))
					.body(update ? String.format(ApiMessage.RECORD_UPDATED.message(), "PESSOA_ID", entity.getId())
							: String.format(ApiMessage.RECORD_CREATED.message(), "PESSOA_ID", entity.getId()));
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new PessoaException(ex.getMessage(), ex);
		}
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.pessoaRepository(new PessoaRepository())
				.tarefaService(new TarefaService())
				.departamentoRepository(new DepartamentoRepository())
				.build();
	}
	
	public PessoaController(ObjectMapper mapperJson,  PessoaRepository pessoaRepository, EmpresaRepository empresaRepository, TarefaRepository tarefaRepository, TarefaService tarefaService,  DepartamentoRepository departamentoRepository, EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository, PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {
		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository, telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
