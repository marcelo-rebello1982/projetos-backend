package br.com.cadastroit.services.web.controllers;

import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.ResponseEntity.ok;

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

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.domain.Tarefa;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.api.services.interfaces.ReportDepartamentoService;
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
import br.com.cadastroit.services.web.dto.DepartamentoDTO;
import br.com.cadastroit.services.web.dto.DepartamentoResumedDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/departamento")
public class DepartamentoController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private ReportDepartamentoService reportDepartamentoService;

	public static final String X_SUGGESTED_FILENAME_HEADER = "X-SUGGESTED-FILENAME";
	
	@Operation(summary = "Get Max Id from Departament")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Departament created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DepartamentoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departament", description = "Get a Departament max id record")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("tarefaId") Long tarefaId) {

		try {
			Long id = departamentoRepository.maxId(entityManagerFactory, tarefaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Get a Departament by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Tarefa", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Tarefa.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Tarefa not found", content = @Content) })
	@Tag(name = "Departament", description = "Get a Departament record by id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Departamento entity = departamentoRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(mountObject(departamentoMapper::toDto, entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Create Departament")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Departament created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = DepartamentoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departament", description = "Create a Departament record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@Validated @RequestBody DepartamentoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			 // Tarefa entity = commonsValidation.recuperarTarefa(tarefaId, entityManagerFactory);
			return mountEntity(dto, null, null, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Update Departament")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Departament updated with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Departamento.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departament", description = "Updating a Departament record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("tarefaId") Long tarefaId, @PathVariable("id") Long id, @Validated @RequestBody DepartamentoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Tarefa entity = commonsValidation.recuperarTarefa(tarefaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Delete Departament By id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "No Content", description = "Delete Departament", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departament", description = "Deleting a Departament record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Long id) throws TarefaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			departamentoRepository.delete(departamentoRepository.findById(id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Search all Departaments")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Departaments", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Departamento.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departamento", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = departamentoRepository.count(null, entityManagerFactory);
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

	@Operation(summary = "Search all Departaments by filters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Departaments", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Departamento.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Departamento", description = "Get Departamento by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody DepartamentoDTO dto) {

		try {
			Long count = departamentoRepository.count(dto, entityManagerFactory);
			if (count > 0) {
				List<Departamento> list = departamentoRepository.findByFilters(dto, entityManagerFactory, requestParams, page, length);

				// List<SelectOptionDTO> itens = Arrays.asList(TipoAtividadeAprovacao.values()).stream()
				// .filter(t -> t.isListar())
				// .sorted(Comparator.comparing(TipoAtividadeAprovacao::getLabel))
				// .map(i -> new SelectOptionDTO(i.name(), i.getLabel()))
				// .collect(Collectors.toList());

				List<DepartamentoResumedDTO> itens = list.stream()
						.filter(d -> !d.getTarefas().isEmpty())
						.map(i -> new DepartamentoResumedDTO(i.getId(), i.getDescr()))
						.collect(Collectors.toList());

				// List<SelectOptionDTO> itens = new ArrayList<>();

				// for (VoucherTipo e : VoucherTipo.values())
				// itens.add(new SelectOptionDTO(e.name(), e.getDescricao()));

				// return itens;

				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(mountObject(departamentoMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Generate Report PDF of Departaments")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Resport PDF", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Byte.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@PostMapping(value = "/exportar-produtos", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void exportarProdutos(@RequestBody DepartamentoDTO dto, HttpServletResponse response) throws Exception {

		response.setContentType("application/download");
	}

	private ResponseEntity<Object> mountEntity(DepartamentoDTO dto, Long id, Tarefa tarefa, boolean update) throws TarefaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Departamento entity = departamentoMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity = departamentoRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new TarefaException(ex.getMessage(), ex);
		}
	}

	@Operation(summary = "Generate Report PDF of Departaments")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Resport PDF", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Byte.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@GetMapping("/pdf/{nameReport}")
	public ResponseEntity<byte[]> generateDepartamentsReportPdf(@PathVariable String nameReport) {

		byte[] relatorio = reportDepartamentoService.generateReportPdf(nameReport);
		return ok().contentType(APPLICATION_PDF) //
				.header(X_SUGGESTED_FILENAME_HEADER, "departamento.pdf")
				.body(relatorio);
	}

	@Operation(summary = "Generate Report CSV of Departaments")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Resport CSV", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Byte.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@GetMapping("/csv")
	public ResponseEntity<byte[]> generateDepartamentsReportCsv() {

		byte[] arquivo = reportDepartamentoService.generateDepartamentoReportCsv();
		return ok().contentType(TEXT_PLAIN) //
				.header(X_SUGGESTED_FILENAME_HEADER, "person.csv")
				.body(arquivo);
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.tarefaRepository(new TarefaRepository())
				.tarefaService(new TarefaService())
				.pessoaRepository(new PessoaRepository())
				.build();
	}
	
	public DepartamentoController(ObjectMapper mapperJson, PessoaRepository pessoaRepository, EmpresaRepository empresaRepository,
			TarefaRepository tarefaRepository, TarefaService tarefaService, DepartamentoRepository departamentoRepository,
			EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository,
			PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {

		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository,
				telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
