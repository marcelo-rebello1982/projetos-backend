package br.com.cadastroit.services.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.api.domain.PedidoStatus;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;
import br.com.cadastroit.services.mongodb.repository.impl.CollectionRelatorioApiRepository;
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
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.web.dto.PortaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/pedido")
public class PedidoController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	@Autowired
	private CollectionRelatorioApiRepository callRelatorioRepository;
	
	public static final String X_SUGGESTED_FILENAME_HEADER = "X-SUGGESTED-FILENAME";
	
	@Operation(summary = "Get port off running this")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Order", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PortaDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Tarefa not found", content = @Content) })
	@Tag(name = "Order", description = "Get a port number")
	@GetMapping("/porta")
	public ResponseEntity<Object> findPort(@Value("${local.server.port}") String porta) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			return ResponseEntity.ok().body(PortaDTO.builder().descr("Requisição respondida pela instância na porta").numeroPorta(porta).build());
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Get a Order by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Order", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Tarefa not found", content = @Content) })
	@Tag(name = "Order", description = "Get a Order record by id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pedido entity = pedidoRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(pedidoMapper.toDTO(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Create Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Create a Order record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("pessoaId") Long pessoaId, @Validated @RequestBody PedidoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = pessoaRepository.findById(pessoaId, entityManagerFactory);
			return mountEntity(dto, pessoaId, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Update Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order updated with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Pedido.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Updating a Order record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("id") Long id, @Validated @RequestBody PedidoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pessoa entity = pessoaRepository.findById(pessoaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Delete Order By id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "No Content", description = "Update Order", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Deleting a Order record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Long id) throws TarefaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			pedidoRepository.delete(pedidoRepository.findById(id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Search all Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Order", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pedido.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = pedidoRepository.count(pessoaId, null, entityManagerFactory);
			if (count > 0) {
				List<Pedido> pedidos = pedidoRepository.findAll(pessoaId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(mountDTO(pedidos));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Update Approved payment Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order paid with successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Approved a Order record")
	@PutMapping("/updateaprovedpayments/{pedidoId}")
	public ResponseEntity<Object> handleUpdateAprovedPayment(@PathVariable Long pedidoId, @RequestBody String pedido) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {

			PedidoDTO entity = this.convertFromJson(pedido, PedidoDTO.class);
			Pessoa pessoa =  this.pessoaMapper.toEntity(entity.getPessoa());
			
			return mountEntity(entity, entity.getId(), pessoa, true);

		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@Operation(summary = "Request a list of reports processed in a period")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order paid with successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "list a Order record")
	@GetMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = true) Map<String, Object> requestParams, @RequestBody(required = true) FiltersDTO filters, @RequestParam(value = "filterOr", required = false) String filterOr, @RequestParam(value = "filterAnd", required = false) String filterAnd, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {

			AtomicReference<List<CollectionRelatorioApi>> collection = new AtomicReference<>(new ArrayList<>());

			List<CollectionRelatorioApi> list = this.callRelatorioRepository.findCollRelatorioApiByFilters(pessoaId, requestParams, filters, filterOr,
					filterAnd, page, length);
			collection.get().addAll(list);
			return new ResponseEntity<Object>(collection.get(), HttpStatus.OK);

		} catch (PedidoException ex) {
			return new ResponseEntity<Object>("Error on process request, [ERROR] = " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ResponseEntity<Object> mountEntity(PedidoDTO dto, Long id, Pessoa pessoa, boolean update) throws PedidoException {

		try {
			
			Pedido entity = this.pedidoMapper.toEntity(dto);
			HttpHeaders headers = new HttpHeaders();
			entity.setPessoa(pessoa);
			entity.setId(update ? id : null);
			entity.setAprovado(isStatusAprovado(entity.getStatus()));
			entity = pedidoRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
			
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new PedidoException(ex.getMessage(), ex);
		}
		
		// aqui vai postar na fila para que o processa-pedido consuma e faça o envio.
		
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.empresaRepository(new EmpresaRepository())
					.pessoaRepository(new PessoaRepository())
						.build();

	}
	
	private List<PedidoDTO> mountDTO(List<Pedido> list) {

		return list.stream()
				.map(this.pedidoMapper::toDTO)
					.collect(Collectors.toList());
	}

	private boolean isStatusAprovado(PedidoStatus status) {

		return PedidoStatus.APROVADO.equals(status);
	}

	public PedidoController(ObjectMapper mapperJson, PessoaRepository pessoaRepository, EmpresaRepository empresaRepository,
			TarefaRepository tarefaRepository, TarefaService tarefaService, DepartamentoRepository departamentoRepository,
			EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository,
			PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository,
			PedidoService pedidoService) {

		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository,
				telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
