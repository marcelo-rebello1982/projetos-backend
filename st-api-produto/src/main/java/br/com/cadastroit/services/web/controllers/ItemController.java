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
import br.com.cadastroit.services.api.domain.Item;
import br.com.cadastroit.services.exceptions.ItemException;
import br.com.cadastroit.services.repositories.EmpresaRepository;
import br.com.cadastroit.services.repositories.ItemRepository;
import br.com.cadastroit.services.repositories.PessoaRepository;
import br.com.cadastroit.services.web.controllers.commons.ApiProdutoCommonsController;
import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
import br.com.cadastroit.services.web.dto.ItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/administracao/item")
public class ItemController extends ApiProdutoCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	public static final String X_SUGGESTED_FILENAME_HEADER = "X-SUGGESTED-FILENAME";

	@Operation(summary = "Get Max Id from Item")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Item created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ItemDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Item", description = "Get a Item max id record")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("empresaId") Long empresaId) {

		try {
			Long id = itemRepository.maxId(entityManagerFactory, empresaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Get a Item by id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the Item", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Item.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
			@ApiResponse(responseCode = "404", description = "Tarefa not found", content = @Content) })
	@Tag(name = "Item", description = "Get a Item record by id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Item entity = itemRepository.findById(id, entityManagerFactory);
			return ResponseEntity.ok().body(mountObject(itemMapper::toDto, entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Create Item")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Item created with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ItemDTO.class)) }),
			@ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Item", description = "Create a Item record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("empresaId") Long empresaId, @Validated @RequestBody ItemDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
			return mountEntity(dto, empresaId, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Update Item")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Item updated with sucessful", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = Item.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Item", description = "Updating a Item record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id, @Validated @RequestBody ItemDTO dto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
			return this.mountEntity(dto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Delete Item By id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "No Content", description = "Delete Item", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseEntity.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Item", description = "Deleting a Item record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Long id) throws ItemException {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			itemRepository.delete(itemRepository.findById(id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@Operation(summary = "Search all Items")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Items", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Item.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Item", description = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("empresaId") Long empresaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length) {

		try {
			Long count = itemRepository.count(empresaId, null, entityManagerFactory);
			if (count > 0) {
				List<Item> list = itemRepository.findAll(empresaId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(mountObject(itemMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@Operation(summary = "Search all Items by filters")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the List of Items", content = {
					@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Item.class))) }),
			@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content) })
	@Tag(name = "Item", description = "Get Item by filters")
	@PostMapping("/findByFilters/{page}/{length}")
	public ResponseEntity<Object> findByFilters(@RequestHeader("empresaId") Long empresaId, @RequestParam(required = false) Map<String, String> requestParams, @PathVariable("page") int page, @PathVariable("length") int length, @RequestBody ItemDTO dto) {

		try {
			Long count = itemRepository.count(empresaId, dto, entityManagerFactory);
			if (count > 0) {
				List<Item> list = itemRepository.findByFilters(empresaId, dto, entityManagerFactory, requestParams, page, length);

				// List<SelectOptionDTO> itens = Arrays.asList(TipoAtividadeAprovacao.values()).stream()
				// .filter(t -> t.isListar())
				// .sorted(Comparator.comparing(TipoAtividadeAprovacao::getLabel))
				// .map(i -> new SelectOptionDTO(i.name(), i.getLabel()))
				// .collect(Collectors.toList());

				// List<ItemDTO> itens = list.stream()
				// .filter(d -> !d.getTarefas().isEmpty())
				// .map(i -> new ItemResumedDTO(i.getId(), i.getDescr()))
				// .collect(Collectors.toList());

				// List<SelectOptionDTO> itens = new ArrayList<>();

				// for (VoucherTipo e : VoucherTipo.values())
				// itens.add(new SelectOptionDTO(e.name(), e.getDescricao()));

				// return itens;

				HttpHeaders headers = httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(mountObject(itemMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(ItemDTO dto, Long id, Empresa empresa, boolean update) throws ItemException {

		try {

			CommonsValidation commonsValidation = this.createCommonsValidation();

			Item entity = itemMapper.toEntity(dto);

			HttpHeaders headers = new HttpHeaders();
			entity.setId(update ? id : null);
			entity.setEmpresa(empresa);
			entity = itemRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
		} catch (NoResultException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new ItemException(ex.getMessage(), ex);
		}
	}

	private CommonsValidation createCommonsValidation() {

		return CommonsValidation.builder().empresaRepository(new EmpresaRepository()).pessoaRepository(new PessoaRepository()).build();
	}

	public ItemController(ObjectMapper mapperJson, EmpresaRepository empresaRepository, ItemRepository itemRepository) {
		super(mapperJson, empresaRepository, itemRepository);
	}
}
