package br.com.cadastroit.services.web.controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.cadastroit.services.api.broker.configuration.RabbitBeanConfiguration;
import br.com.cadastroit.services.api.broker.consumers.model.RelatorioMessage;
import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.api.domain.PedidoStatus;
import br.com.cadastroit.services.api.domain.Pessoa;
import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.aws.AwsBucketUploadModel;
import br.com.cadastroit.services.common.util.UtilDate;
import br.com.cadastroit.services.converters.ConverterMessage;
import br.com.cadastroit.services.exceptions.PedidoException;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.mongodb.domain.CollectionRelatorioApi;
import br.com.cadastroit.services.mongodb.repository.impl.CollectionRelatorioApiRepository;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.ItemPedidoRepository;
import br.com.cadastroit.services.repositories.ItemRepository;
import br.com.cadastroit.services.repositories.PedidoRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.web.controllers.commons.ApiCadastroCommonsController;
import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
import br.com.cadastroit.services.web.dto.FiltersDTO;
import br.com.cadastroit.services.web.dto.PedidoDTO;
import br.com.cadastroit.services.web.dto.PessoaDTO;
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
	private CollectionRelatorioApiRepository collectionRelatorioApiRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	private final Logger logger = Logger.getLogger(PedidoController.class);

	@Operation(summary = "Get Max Id from Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Get a Order max id record")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("pessoaId") Long pessoaId) {

		try {
			Long id = pedidoRepository.maxId(entityManagerFactory, pessoaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
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
				List<Pedido> pedidos = pedidoRepository.findAll(entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(pedidoMapper.toDTO(pedidos));
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
	public ResponseEntity<Object> findByFilters(@RequestHeader("pessoaId") Long pessoaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody PedidoDTO dto) {

		try {
			Long count = pedidoRepository.count(pessoaId, dto, entityManagerFactory);
			if (count > 0) {
				List<Pedido> list = pedidoRepository.findByFilters(pessoaId, dto, entityManagerFactory, requestParams, page, length);

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

	// aqui vai enviar a aprovação do pagamento para a api-cadastro.
	@Operation(summary = "Update Approved payment Order")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order paid with successfully", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Order", description = "Approved a Order record")
	@PutMapping("/updateaprovedpayments/{pedidoId}")
	public ResponseEntity<Object> handleUpdateAprovedPayment(@RequestHeader("pessoaId") Long pessoaId, @PathVariable("pedidoId") Long pedidoId, @RequestParam(required = false) Map<String, Object> requestParams, @RequestBody PedidoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {

			Pedido pedido = pedidoRepository.findById(pedidoId, pessoaId, entityManagerFactory);
			Pessoa pessoa = pessoaRepository.findById(pessoaId, entityManagerFactory);
			return this.mountEntity(pedido, pessoa, requestParams, dto.getStatus(), true);

		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

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

	private ResponseEntity<Object> mountEntity(Pedido entity, Pessoa pessoa, Map<String, Object> requestParams, br.com.cadastroit.services.api.domain.PedidoStatus status, boolean update)
			throws PedidoException {

		if (entity != null) {

			String protocolo = "";
			CommonsValidation commonsValidation = this.createCommonsValidation();

			try {

				HttpHeaders headers = new HttpHeaders();
				entity.setPessoa(pessoa);
				entity.setId(update ? entity.getId() : null);
				entity.setAprovado(isStatusAprovado(status));
				entity.setStatus(isStatusAprovado(status) ? PedidoStatus.APROVADO : PedidoStatus.EM_ANDAMENTO);

				// aqui envia para o cadastro o objeto com status aprovado
				entity = pedidoRepository.handleUpdateAprovedPayment(entity, entityManagerFactory);

				AwsBucketUploadModel model = AwsBucketUploadModel.builder()
						.code(0)
						.uuid(UUID.randomUUID())
						.nroProtocolo(String.valueOf(System.currentTimeMillis()))
						.pessoaId(pessoa.getId())
						.build();

				HashMap<String, String> reference = new HashMap<>();
				reference.put(pessoa.getId().toString(), null);

				this.insertDocumentMongo(model);

				if (isStatusAprovado(status)) {
					protocolo = this.sendMessageToRabbit(model, requestParams);
				}

				headers.add("Protocolo", "/find/" + protocolo);
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

	public void insertDocumentMongo(AwsBucketUploadModel model) throws PedidoException {

		try {

			GsonBuilder builder = new GsonBuilder();
			builder.setLenient();
			Gson gSonDesif = builder.create();
			String modelRetornado = objectToJson(model);
			CollectionRelatorioApi requestObject = gSonDesif.fromJson(objectToJson(model), CollectionRelatorioApi.class);
			requestObject.setID(model.getUuid().toString());
			requestObject.setSTATUS(model.getCode());
			requestObject.setPESSOA_ID(model.getPessoaId());
			requestObject.setCREATIONDATE(UtilDate.toDateTimeStringUTC(Timestamp.from(Instant.now())));
			requestObject.setTYPEARCHIVE("JSON");
			requestObject.setNROPROTOCOLO(model.getNroProtocolo());
			Map<String, Object> map = requestObject.toMap(requestObject);
			this.collectionRelatorioApiRepository.insertDocument("relatoriocontasdata", map);

		} catch (Exception ex) {
			throw new PedidoException(String.format("ERROR ON INSERT DATA IN MONGO DATABASE " + ex.getMessage()));
		}
	}

	private String sendMessageToRabbit(AwsBucketUploadModel model, Map<String, Object> requestParams) throws Exception {

		try {

			RelatorioMessage message = RelatorioMessage.builder()
					.id(model.getUuid().toString())
					.status(0)
					.requestParams(requestParams)
					.page(1)
					.lenght(100)
					.description(EMPTY_MSG)
					.pessoaId(model.getPessoaId())
					.nroProtocolo(Long.valueOf(model.getNroProtocolo()))
					.build();
			
			rabbitTemplate.convertAndSend(RabbitBeanConfiguration.exchangePedidoAprovado, RabbitBeanConfiguration.rkPedidoAprovado,
					ConverterMessage.builder().build().convertToBytes(message).getMessageBytes());

			return String.valueOf(message.getNroProtocolo());

		} catch (Exception ex) {
			throw new PedidoException(String.format("ERRO AO ENVIAR MENSAGEM PARA O RABBIT " + ex.getMessage()));
		}
	}

	private ResponseEntity<Object> mountEntity(PedidoDTO dto, Long id, Pessoa pessoa, boolean update) throws PedidoException {

		try {

			Pedido entity = pedidoMapper.toEntity(dto);
			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity.setPessoa(pessoa);
			entity = pedidoRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new TarefaException(ex.getMessage(), ex);
		}
	}

	private boolean isStatusAprovado(br.com.cadastroit.services.api.domain.PedidoStatus status) {

		return PedidoStatus.APROVADO.equals(status);
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder()
				.empresaRepository(new EmpresaRepository())
				.pessoaRepository(new PessoaRepository())
				.itemRepository(new ItemRepository())
				.pedidoService(new PedidoService())
				.build();
	}

	public PedidoController(ObjectMapper mapperJson, EmpresaRepository empresaRepository, PessoaRepository pessoaRepository,
			PedidoRepository pedidoRepository, ItemRepository itemRepository, ItemPedidoRepository itemPedidoRepository) {

		super(mapperJson, empresaRepository, pessoaRepository, pedidoRepository, itemRepository, itemPedidoRepository);
	}

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
}
