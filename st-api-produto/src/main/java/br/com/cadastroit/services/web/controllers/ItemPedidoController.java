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

import br.com.cadastroit.services.api.domain.ItemPedido;
import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.exceptions.TarefaException;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.ItemPedidoRepository;
import br.com.cadastroit.services.repositories.ItemRepository;
import br.com.cadastroit.services.repositories.PedidoRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.web.controllers.commons.ApiCadastroCommonsController;
import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
import br.com.cadastroit.services.web.dto.ItemPedidoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/item/itempedido")
public class ItemPedidoController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public static final String X_SUGGESTED_FILENAME_HEADER = "X-SUGGESTED-FILENAME";

	@Operation(summary = "Get Max Id from OrderedItem")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "OrderedItem created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ItemPedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "OrderedItem", description = "Get a OrderedItem max id record")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("pedidoId") Long pedidoId) {

		try {
			Long id = itemPedidoRepository.maxId(entityManagerFactory, pedidoId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Get a OrderedItem by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Tarefa", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ItemPedido.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Tarefa not found", content = @Content) })
	@Tag(name = "OrderedItem", description = "Get a OrderedItem record by id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			ItemPedido entity = itemPedidoRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(itemPedidoMapper.toResumeDTO(entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Create OrderedItem")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "OrderedItem created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ItemPedidoDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "OrderedItem", description = "Create a OrderedItem record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("pedidoId") Long pedidoId, @Validated @RequestBody ItemPedidoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pedido entity = commonsValidation.recuperarPedido(pedidoId, entityManagerFactory);
			return mountEntity(dto, pedidoId, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Update OrderedItem")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "OrderedItem updated with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ItemPedidoDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "OrderedItem", description = "Updating a OrderedItem record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("pedidoId") Long pedidoId, @PathVariable("id") Long id, @Validated @RequestBody ItemPedidoDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Pedido entity = commonsValidation.recuperarPedido(pedidoId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Delete OrderedItem By id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "No Content", description = "Delete OrderedItem", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "OrderedItem", description = "Deleting a OrderedItem record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Long id) throws TarefaException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			itemPedidoRepository.delete(itemPedidoRepository.findById(id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Search all OrderedItem")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of OrderedItem", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemPedido.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "OrderedItem", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("pedidoId") Long pedidoId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = itemPedidoRepository.count(pedidoId, null, entityManagerFactory);
			if (count > 0) {
				List<ItemPedido> list = itemPedidoRepository.findAll(entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(itemPedidoMapper.toDTO(list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Search all OrderedItem by filters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of OrderedItem", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ItemPedido.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "OrderedItem", description = "Get OrderedItem by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("pedidoId") Long pedidoId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody ItemPedidoDTO dto) {

		try {
			Long count = itemPedidoRepository.count(pedidoId, dto, entityManagerFactory);
			if (count > 0) {
				List<ItemPedido> list = itemPedidoRepository.findByFilters(dto, entityManagerFactory, requestParams, page, length);

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
				return ResponseEntity.ok().headers(headers).body(itemPedidoMapper.toDTO(list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(ItemPedidoDTO dto, Long id, Pedido pedido, boolean update) throws TarefaException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			ItemPedido entity = itemPedidoMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity.setPedido(pedido);
			entity = itemPedidoRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new TarefaException(ex.getMessage(), ex);
		}
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

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder().pedidoRepository(new PedidoRepository()).itemRepository(new ItemRepository()).build();
	}

	public ItemPedidoController(ObjectMapper mapperJson, EmpresaRepository empresaRepository, PessoaRepository pessoaRepository,
			PedidoRepository pedidoRepository, ItemRepository itemRepository, ItemPedidoRepository itemPedidoRepository) {
		super(mapperJson, empresaRepository, pessoaRepository, pedidoRepository, itemRepository, itemPedidoRepository);
	}
}
