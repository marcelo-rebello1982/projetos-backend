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

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Telefone;
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
import br.com.cadastroit.services.web.dto.TelefoneDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/pessoa/telefone")
public class TelefoneController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Tag(name = "Contato", description = "Get maxId Contato")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("pessoaId") Long pessoaId) {

		try {
			Long id = telefoneRepository.maxId(entityManagerFactory, pessoaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Contato", description = "Get Contato by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Telefone entity = telefoneRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(telefoneMapper.toDto(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Contato", description = "Get Contato by Id")
	@GetMapping("/find/{pessoaId}/{id}")
	public ResponseEntity<Object> find(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Telefone entity = telefoneRepository.findById(pessoaId, id, entityManagerFactory);
			return ResponseEntity.ok().body(telefoneMapper.toDto(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Contato", description = "Create a new Contato record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("pessoaId") Long pessoaId, @Validated @RequestBody TelefoneDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = commonsValidation.recuperarPessoa(pessoaId, entityManagerFactory);
			return mountEntity(dto, null, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Contato", description = "Updating a Contato record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id, @Validated @RequestBody TelefoneDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = commonsValidation.recuperarPessoa(pessoaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Contato", description = "Deleting a Contato record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id) throws PessoaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			telefoneRepository.delete(telefoneRepository.findById(pessoaId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Contato", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = telefoneRepository.count(pessoaId, null, entityManagerFactory);
			if (count > 0) {
				List<Telefone> list = telefoneRepository.findAll(pessoaId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(telefoneMapper.toDto(list));

			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Tag(name = "Contato", description = "Get Contato by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, TelefoneDTO dto) {

		try {

			Long count = telefoneRepository.count(pessoaId, dto, entityManagerFactory);

			if (count > 0) {
				List<Telefone> contatos = telefoneRepository.findByFilters(pessoaId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(telefoneMapper.toDto(contatos));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(TelefoneDTO dto, Long id, Pessoa pessoa, boolean update) throws PessoaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Telefone entity = telefoneMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setPessoa(pessoa);
			entity.setId(update ? id : null);
			entity = telefoneRepository.save(entity);
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
				.tarefaService(new TarefaService())
				.departamentoRepository(new DepartamentoRepository())
				.build();
	}

	public TelefoneController(ObjectMapper mapperJson,  PessoaRepository pessoaRepository, EmpresaRepository empresaRepository, TarefaRepository tarefaRepository, TarefaService tarefaService,  DepartamentoRepository departamentoRepository, EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository, PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {
		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository, telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}

}
