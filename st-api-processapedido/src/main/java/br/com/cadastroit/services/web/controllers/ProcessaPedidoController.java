package br.com.cadastroit.services.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import br.com.cadastroit.services.api.services.ProcessaPedidoService;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;
import br.com.cadastroit.services.mongodb.repository.impl.CollectionRelatorioApiRepository;
import br.com.cadastroit.services.repositories.ProcessaPedidoRepository;
import br.com.cadastroit.services.web.controllers.commons.ApiProcessaPedidoCommonsController;
import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
import br.com.cadastroit.services.web.dto.FiltersDTO;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.web.dto.PedidoInformacoesEnvioDTO;
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
public class ProcessaPedidoController extends ApiProcessaPedidoCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private CollectionRelatorioApiRepository collectionRelatorioApiRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private final Logger logger = Logger.getLogger(ProcessaPedidoController.class);

	@Operation(summary = "Search all Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Order", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pedido.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = processaPedidoRepository.count(pessoaId, null, entityManagerFactory);
			if (count > 0) {
				List<Pedido> pedidos = processaPedidoRepository.findAll(entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pedidoMapper.toDTO(pedidos));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}
	
	@Operation(summary = "Search all Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Order", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PedidoInformacoesEnvioDTO.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Get all records using pagination mechanism")
	@GetMapping("/allResumed/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody FiltersDTO filters) {

		try {
			
			EntityManager entityManager = entityManagerFactory.createEntityManager();
	        EntityManager managers = entityManagerFactory.createEntityManager();
	        
			Long count = processaPedidoRepository.count(pessoaId, requestParams, page, length, true, filters, entityManager, managers);
			if (count > 0) {
				List<PedidoInformacoesEnvioDTO> pedidoInformacoes = processaPedidoRepository.findAll(entityManagerFactory, requestParams, pessoaId, page, length, filters);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pedidoInformacoes);
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Search all Order by filters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Departaments", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Pedido.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Get Order by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody PedidoDTO dto) {

		try {
			Long count = processaPedidoRepository.count(pessoaId, dto, entityManagerFactory);
			if (count > 0) {
				List<Pedido> list = processaPedidoRepository.findByFilters(pessoaId, dto, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pedidoMapper.toDTO(list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "List Approved payment Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order paid with successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = CollectionRelatorioApi.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Request a list of reports processed in a period")
	@GetMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = true) Map<String, Object> requestParams, @RequestBody(required = true) FiltersDTO filters, @RequestParam(value = "filterOr", required = false) String filterOr, @RequestParam(value = "filterAnd", required = false) String filterAnd, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {

			AtomicReference<List<CollectionRelatorioApi>> collection = new AtomicReference<>(new ArrayList<>());
			List<CollectionRelatorioApi> list = this.collectionRelatorioApiRepository.findCollRelatorioApiByFilters(pessoaId, requestParams, filters,
					filterOr, filterAnd, page, length);
			collection.get().addAll(list);
			return new ResponseEntity<Object>(collection.get(), HttpStatus.OK);

		} catch (Exception ex) {
			return new ResponseEntity<Object>("Error on process request, [ERROR] = " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Update Sent Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order paid with successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Approved a Order record")
	@PutMapping("/handleupdatesentorder/{pedidoId}")
	public ResponseEntity<Object> handleUpdateSentOrder(@PathVariable("pedidoId") Long pedidoId, @RequestParam(required = false) Map<String, Object> requestParams, @RequestBody PedidoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {

			Pedido pedido = processaPedidoRepository.findById(pedidoId, entityManagerFactory);
			return this.mountEntity(pedido, requestParams, dto.getStatus(), true);

		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	// List<SelectOptionDTO> itens = Arrays.asList(TipoAtividadeAprovacao.values()).stream()
	// .filter(t -> t.isListar())
	// .sorted(Comparator.comparing(TipoAtividadeAprovacao::getLabel))
	// .map(i -> new SelectOptionDTO(i.name(), i.getLabel()))
	// .collect(Collectors.toList());

	// List<DepartamentoResumedDTO> itens = list.stream()
	// .filter(d -> !d.getTarefas().isEmpty())
	// .map(i -> new DepartamentoResumedDTO(i.getId(), i.getDescr()))
	// .collect(Collectors.toList());

	// List<SelectOptionDTO> itens = new ArrayList<>();

	// for (VoucherTipo e : VoucherTipo.values())
	// itens.add(new SelectOptionDTO(e.name(), e.getDescricao()));

	// return itens;

	// @Operation(summary = "Generate Report PDF of Departaments")
	// @ApiResponses(value = {
	// @ApiResponse(responseCode = "200", description = "Found the Resport PDF", content = {
	// @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Byte.class))) }),
	// @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	// @GetMapping("/pdf/{nameReport}")
	// public ResponseEntity<byte[]> generateDepartamentsReportPdf(@PathVariable String nameReport) {
	//
	// byte[] relatorio = reportDepartamentoService.generateReportPdf(nameReport);
	// return ok().contentType(APPLICATION_PDF) //
	// .header(X_SUGGESTED_FILENAME_HEADER, "departamento.pdf")
	// .body(relatorio);
	// }
	//
	// @Operation(summary = "Generate Report CSV of Departaments")
	// @ApiResponses(value = {
	// @ApiResponse(responseCode = "200", description = "Found the Resport CSV", content = {
	// @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Byte.class))) }),
	// @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	// @GetMapping("/csv")
	// public ResponseEntity<byte[]> generateDepartamentsReportCsv() {
	//
	// byte[] arquivo = reportDepartamentoService.generateDepartamentoReportCsv();
	// return ok().contentType(TEXT_PLAIN) //
	// .header(X_SUGGESTED_FILENAME_HEADER, "person.csv")
	// .body(arquivo);
	// }

	@Operation(summary = "Generate Report PDF of Departaments")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Resport PDF", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Byte.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Generate Report PDF of Orders")
	@PostMapping(value = "/exportar-produtos", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public void exportarProdutos(@RequestBody PedidoDTO dto, HttpServletResponse response) throws Exception {

		response.setContentType("application/download");
	}

	private ResponseEntity<Object> mountEntity(Pedido entity, Map<String, Object> requestParams, PedidoStatus status, boolean update)
			throws PedidoException {

		if (entity != null) {

			String protocolo = "";
			CommonsValidation commonsValidation = this.createCommonsValidation();

			try {

				HttpHeaders headers = new HttpHeaders();
				entity.setId(update ? entity.getId() : null);

				// aqui envia para o cadastro o objeto com status aprovado
				// entity = pedidoRepository.handleUpdateAprovedPayment(entity, entityManagerFactory);

				// AwsBucketUploadModel model = AwsBucketUploadModel.builder()
				// .code(0)
				// .uuid(UUID.randomUUID())
				// .nroProtocolo(String.valueOf(System.currentTimeMillis()))
				// .pessoaId(pessoa.getId())
				// .build();

				// HashMap<String, String> reference = new HashMap<>();
				// reference.put(pessoa.getId().toString(), null);

				// this.insertDocumentMongo(model);

				headers.add("Location", "/find/" + entity.getId());
				return new ResponseEntity<>(commonsValidation.createResponseTemplate("Relatório em processamento...", Long.valueOf(protocolo)),
						HttpStatus.ACCEPTED);
			} catch (Exception ex) {
				return new ResponseEntity<>(commonsValidation.createResponseTemplate(String.format(ex.getMessage())), HttpStatus.BAD_REQUEST);
			}
		} else {
			throw new NoResultException("Dados informados inválidos");
		}
	}

	// public void insertDocumentMongo(AwsBucketUploadModel model) throws PedidoException {
	//
	// try {
	//
	// GsonBuilder builder = new GsonBuilder();
	// builder.setLenient();
	// Gson gSonDesif = builder.create();
	// String modelRetornado = objectToJson(model);
	// CollectionRelatorioApi requestObject = gSonDesif.fromJson(objectToJson(model), CollectionRelatorioApi.class);
	// requestObject.setID(model.getUuid().toString());
	// requestObject.setSTATUS(model.getCode());
	// requestObject.setPESSOA_ID(model.getPessoaId());
	// requestObject.setCREATIONDATE(UtilDate.toDateTimeStringUTC(Timestamp.from(Instant.now())));
	// requestObject.setTYPEARCHIVE("JSON");
	// requestObject.setNROPROTOCOLO(model.getNroProtocolo());
	// Map<String, Object> map = requestObject.toMap(requestObject);
	// this.collectionRelatorioApiRepository.insertDocument("relatoriocontasdata", map);
	//
	// } catch (Exception ex) {
	// throw new PedidoException(String.format("ERROR ON INSERT DATA IN MONGO DATABASE " + ex.getMessage()));
	// }
	// }

	private ResponseEntity<Object> mountEntity(PedidoDTO dto, Long id, boolean update) throws PedidoException {

		try {

			Pedido entity = pedidoMapper.toEntity(dto);
			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity = processaPedidoRepository.save(entity);
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
				.processaPedidoRepository(new ProcessaPedidoRepository())
				.processaPedidoService(new ProcessaPedidoService())
				.build();
	}

	public ProcessaPedidoController(ObjectMapper mapperJson, ProcessaPedidoRepository processaPedidoRepository,
			ProcessaPedidoService processaPedidoService) {

		super(mapperJson, processaPedidoRepository, processaPedidoService);
	}

}
