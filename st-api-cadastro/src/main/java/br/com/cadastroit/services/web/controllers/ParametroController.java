package br.com.cadastroit.services.web.controllers;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import br.com.cadastroit.services.api.domain.Parametro;
import br.com.cadastroit.services.api.domain.ParametroChaveType;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.exceptions.ParametroException;
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
import br.com.cadastroit.services.web.dto.ParametroDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping(value = "/administracao/parametro", produces = MediaType.APPLICATION_JSON_VALUE)
public class ParametroController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Tag(name = "Parametro", description = "Get status from API - Consulta Parametro")
	@GetMapping("/status")
	public ResponseEntity<Object> status(@RequestParam(name = "status", required = false, defaultValue = "UP") String status) {

		try {
			return new ResponseEntity<>(HealthCheck.builder().status(status).maxId(parametroRepository.maxId(entityManagerFactory)).build(),
					HttpStatus.OK);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Parametro", description = "Get maxId Parametro")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId() {

		try {
			Long id = parametroRepository.maxId(entityManagerFactory);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	// @Tag(name = "Parametro", description = "Create a new Parametro record")
	// @PostMapping("/create")
	// public ResponseEntity<Object> handlePost(@RequestHeader("empresaId") Long empresaId, @Validated @RequestBody ParametroDto
	// dto) {
	//
	// CommonsValidation commonsValidation = this.createCommonsValidation();
	//
	// try {
	// Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
	// return mountEntity(dto, null, entity, false);
	// } catch (Exception ex) {
	// return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
	// }
	// }

	@Tag(name = "Parametro", description = "Updating a Parametro record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id, @Validated @RequestBody ParametroDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Parametro", description = "Deleting a Parametro record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) throws ParametroException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			parametroRepository.delete(parametroRepository.findById(empresaId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Parametro", description = "Get Parametro by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader(value = "empresaId", required = false) Long empresaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody ParametroDTO dto) {

		try {

			Long count = parametroRepository.count(dto, empresaId, entityManagerFactory);

			if (count > 0) {
				List<Parametro> parametros = parametroRepository.findByFilters(empresaId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(parametroMapper.toListDto(parametros));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@GetMapping(value = "/all")
	public ResponseEntity<Object> findAll(@RequestParam(required = false) Map<String, String> requestParams, HttpServletRequest req) {

		try {
			List<ParametroDTO> parametros = parametroRepository.obterParametros();
			return ResponseEntity.ok().body(CollectionUtils.isNotEmpty(parametros) ? parametros : EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@GetMapping(value = "/obterPorChaves/{chaves}")
	public ResponseEntity<Object> obterPorChaves(@PathVariable(required = true) List<ParametroChaveType> chaves) {

		try {
			List<ParametroDTO> parametros = parametroRepository.obterParametros(chaves);
			return ResponseEntity.ok().body(CollectionUtils.isNotEmpty(parametros) ? parametros : EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Tag(name = "Parametro", description = "Get by Key records using pagination mechanism")
	@GetMapping(value = "/obterPorChave/{chave}")
	public ResponseEntity<Object> obterPorChave(@PathVariable(required = true) ParametroChaveType chave) {

		try {

			List<ParametroDTO> parametros = parametroRepository.obterParametros(chave);
			return ResponseEntity.ok().body(CollectionUtils.isNotEmpty(parametros) ? parametros : EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@GetMapping(value = "/obterPorChave/{chave}/empresa/{empresaId}")
	public ResponseEntity<Object> obterPorChaveAndEmpresa(@PathVariable ParametroChaveType chave, @PathVariable(name = "empresaId") Long empresaId) {

		try {

			ParametroDTO parametro = parametroRepository.obterPorChaveAndEmpresa(empresaId, chave, entityManagerFactory);
			return ResponseEntity.ok().body(parametro != null ? parametro : EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}
	
	@GetMapping(value = "/obterPorChaves/{chaves}/empresa/{empresaId}")
	public ResponseEntity<Object> obterPorChavesAndEmpresa(@PathVariable List<ParametroChaveType> chaves, @PathVariable(name = "empresaId") Long empresaId) {

		try {

			List<ParametroDTO> parametros = parametroRepository.obterPorChavesAndEmpresa(empresaId, chaves, entityManagerFactory);
			return ResponseEntity.ok().body(CollectionUtils.isNotEmpty(parametros) ? parametros : EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(ParametroDTO dto, Long id, Empresa empresa, boolean update) throws ParametroException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Parametro entity = null; // new Para parametroMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			// entity.setEmpresa(empresa != null ? empresa : null);
			entity.setId(update ? id : null);
			entity = parametroRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new ParametroException(ex.getMessage(), ex);
		}
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder().tarefaService(new TarefaService()).departamentoRepository(new DepartamentoRepository()).build();
	}

	public ParametroController(ObjectMapper mapperJson, PessoaRepository pessoaRepository, EmpresaRepository empresaRepository,
			TarefaRepository tarefaRepository, TarefaService tarefaService, DepartamentoRepository departamentoRepository,
			EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository, PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {

		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository,
				telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
