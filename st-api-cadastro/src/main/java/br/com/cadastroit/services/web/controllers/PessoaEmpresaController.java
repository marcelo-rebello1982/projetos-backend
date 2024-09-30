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

import br.com.cadastroit.services.api.domain.Empresa;
import br.com.cadastroit.services.api.domain.HealthCheck;
import br.com.cadastroit.services.api.domain.PessoaEmpresa;
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
import br.com.cadastroit.services.web.dto.FiltersDTO;
import br.com.cadastroit.services.web.dto.PessoaEmpresaDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/pessoaempresa")
public class PessoaEmpresaController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	
	@GetMapping("/status")
	public ResponseEntity<Object> status(@RequestParam(name = "status", required = false, defaultValue = "UP") String status) {

		try {
			return new ResponseEntity<>(HealthCheck.builder().status(status).maxId(pessoaEmpresaRepository.maxId(entityManagerFactory)).build(),
					HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Tag(name = "PessoaEmpresa", description = "Get maxId PessoaEmpresa")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("empresaId") Long empresaId) {

		try {
			Long id = pessoaEmpresaRepository.maxId(entityManagerFactory, empresaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}
	
	@Tag(name = "PessoaEmpresa", description = "Get PessoaEmpresa by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id, @RequestHeader(value = "token", required = false) String token, @RequestBody FiltersDTO filterDto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();
        Integer filters = filterDto.getFilters() == null ? 0 : filterDto.getFilters();

		try {
			
	       // HashMap<Boolean, String> result = this.validarCredencial(token, filters, null);
           // boolean key = result.entrySet().iterator().next().getKey();

			PessoaEmpresa entity = pessoaEmpresaRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(pessoaEmpresaMapper.toPessoaEmpresaResume(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
//	@Tag(name = "PessoaEmpresa", description = "Get PessoaEmpresa by Id")
//	@GetMapping("/findByQueryParam/{id}/{empresaId}/{descr}")
//	public ResponseEntity<Object> findByQueryParam(@PathVariable("id") Long id, @PathVariable("empresaId") Long empresaId, @PathVariable("descr") String  descr) {
//
//		CommonsValidation commonsValidation = this.createCommonsValidation();
//
//		try {
//			PessoaEmpresa entity = pessoaEmpresaRepository.findByQueryParam(id, empresaId, descr);
//			return ResponseEntity.ok().body(pessoaEmpresaMapper.toPessoaDto(entity));
//		} catch (Exception ex) {
//			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
//		}
//	}

	@Tag(name = "PessoaEmpresa", description = "Get PessoaEmpresa by Id")
	@GetMapping("/find/empresa/{id}")
	public ResponseEntity<Object> find(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			PessoaEmpresa entity = pessoaEmpresaRepository.findById(empresaId, id, entityManagerFactory);
			return ResponseEntity.ok().body(pessoaEmpresaMapper.toPessoaEmpresaResume(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "PessoaEmpresa", description = "Create a new PessoaEmpresa record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("empresaId") Long empresaId, @Validated @RequestBody PessoaEmpresaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
			return mountEntity(dto, null, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "PessoaEmpresa", description = "Updating a PessoaEmpresa record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id, @Validated @RequestBody PessoaEmpresaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "PessoaEmpresa", description = "Deleting a PessoaEmpresa record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) throws PessoaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			pessoaEmpresaRepository.delete(pessoaEmpresaRepository.findById(empresaId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "PessoaEmpresa", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("empresaId") Long empresaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = pessoaEmpresaRepository.count(empresaId, null, entityManagerFactory);
			if (count > 0) {
				List<PessoaEmpresa> list = pessoaEmpresaRepository.findAll(empresaId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pessoaEmpresaMapper.toDto(list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Tag(name = "PessoaEmpresa", description = "Get PessoaEmpresa by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader(value ="empresaId", required= false) Long empresaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody PessoaEmpresaDTO dto) {

		try {
			
			Long count = pessoaEmpresaRepository.count(empresaId, dto, entityManagerFactory);
			
			if (count > 0) {
				List<PessoaEmpresa> pessoaEmpresa = pessoaEmpresaRepository.findByFilters(empresaId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok()
						.headers(headers)
						.body(pessoaEmpresaMapper.toDto(pessoaEmpresa));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(PessoaEmpresaDTO dto, Long id, Empresa empresa, boolean update) throws PessoaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			PessoaEmpresa entity = pessoaEmpresaMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setEmpresa(empresa);
			entity.setId(update ? id : null);
			entity = pessoaEmpresaRepository.save(entity);
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
				.pessoaEmpresaRepository(new PessoaEmpresaRepository())
				.tarefaService(new TarefaService())
				.departamentoRepository(new DepartamentoRepository())
				.build();
	}
	
	public PessoaEmpresaController(ObjectMapper mapperJson,  PessoaRepository pessoaRepository, EmpresaRepository empresaRepository, TarefaRepository tarefaRepository, TarefaService tarefaService,  DepartamentoRepository departamentoRepository, EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository, PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {
		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository, telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
