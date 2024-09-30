package br.com.cadastroit.services.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

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

import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.api.enums.BalancoEstoqueStatus;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.exceptions.TarefaException;
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
import br.com.cadastroit.services.web.dto.EnumDTO;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/pessoa/tarefa")
public class TarefaController extends ApiCadastroCommonsController {
	
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Tag(name = "Tarefa", description = "Get maxId Tarefa")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("pessoaId") Long pessoaId) {
		try {
			Long id = tarefaRepository.maxId(entityManagerFactory, pessoaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}
	
	@Tag(name = "Tarefa", description = "Get Tarefa by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Tarefa entity = tarefaRepository.findById(pessoaId, id, entityManagerFactory);
			return ResponseEntity.ok().body(mountObject(tarefaMapper::toDto, entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@Tag(name = "Tarefa", description = "Create a new Tarefa record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("pessoaId") Long pessoaId, @Validated @RequestBody TarefaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = commonsValidation.recuperarPessoa(pessoaId, entityManagerFactory);
			return mountEntity(dto, pessoaId, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Tarefa", description = "Updating a Pessoa record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id, @Validated @RequestBody TarefaDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = commonsValidation.recuperarPessoa(pessoaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Tarefa", description = "Deleting a Pessoa record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id) throws TarefaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			pessoaRepository.delete(pessoaRepository.findById(pessoaId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Tag(name = "Tarefa", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader(required = false, value = "pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = tarefaRepository.count(pessoaId, null, entityManagerFactory);
			if (count > 0) {
				
				List<Tarefa> list = tarefaRepository.findAll(pessoaId, entityManagerFactory, requestParams, page, length);
				
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body((tarefaService.calculaTotalTarefas((list))));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}
	
	@GetMapping(value = "/exportar/{page}/{length}", produces = MediaType.APPLICATION_JSON_VALUE)
	public void exportar(@RequestHeader("pessoaId") Long pessoaId, @RequestBody TarefaDTO dto, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, HttpServletResponse response)
			throws Exception {

		List<Tarefa> tarefas = tarefaRepository.findByFilters(pessoaId, dto, entityManagerFactory, requestParams, page, length);

		byte[] file = this.tarefaService.exportar(tarefas, dto.getTipoArquivo());
		
		
		tarefas = tarefas.stream()
				.filter(t -> Boolean.FALSE.equals(t.getEncerrado()))
				.sorted((a, b) -> Long.compare(a.getTotalTarefas(), b.getTotalTarefas()))
				.limit(100)
				.collect(Collectors.toList());

		response.setContentType("application/download");
		response.setContentLength(file.length);
		response.getOutputStream().write(file);
	}

	@Tag(name = "Tarefa", description = "Get Tarefa by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody TarefaDTO dto) {

		try {
			Long count = tarefaRepository.count(pessoaId, dto, entityManagerFactory);
			if (count > 0) {
				List<Tarefa> list = tarefaRepository.findByFilters(pessoaId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(mountObject(tarefaMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}
	
	@GetMapping(value = "/enum/balanco")
	public ResponseEntity<List<EnumDTO>> obterOperacoesBalancoEstoque() {

		List<EnumDTO> dtos = new ArrayList<>();

		for (BalancoEstoqueStatus value : BalancoEstoqueStatus.values())
			dtos.add(new EnumDTO(value.name(), value.getDescricao()));

		return new ResponseEntity<>(dtos, HttpStatus.OK);
	}
	
	private ResponseEntity<Object> mountEntity(TarefaDTO dto, Long id, Pessoa pessoa, boolean update) throws TarefaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Tarefa entity = tarefaMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setPessoa(pessoa);
			entity.setId(update ? id : null);
			entity = tarefaRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new TarefaException(ex.getMessage(), ex);
		}
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.tarefaRepository(new TarefaRepository())
				.tarefaService(new TarefaService())
				.pessoaRepository(new PessoaRepository())
				.build();
	}
	
	public TarefaController(ObjectMapper mapperJson,  PessoaRepository pessoaRepository, EmpresaRepository empresaRepository, TarefaRepository tarefaRepository, TarefaService tarefaService,  DepartamentoRepository departamentoRepository, EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository, PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {
		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository, telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
