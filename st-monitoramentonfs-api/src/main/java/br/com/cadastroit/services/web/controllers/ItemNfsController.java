package br.com.cadastroit.services.web.controllers;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import org.mapstruct.factory.Mappers;
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

import br.com.cadastroit.services.api.domain.ItemNfs;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.exceptions.ItemNfsException;
import br.com.cadastroit.services.repositories.CommonsRepository;
import br.com.cadastroit.services.repositories.ItemNfsRepository;
import br.com.cadastroit.services.repositories.NfServRepository;
import br.com.cadastroit.services.web.controller.commons.CommonsValidation;
import br.com.cadastroit.services.web.controller.commons.NfServCommonsController;
import br.com.cadastroit.services.web.dto.ItemNfsDto;
import br.com.cadastroit.services.web.dto.NfServDto;
import br.com.cadastroit.services.web.mapper.ItemNfsMapper;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@Validated
@RestController
@RequestMapping("/administracao/nfserv/itemnfs")
@AllArgsConstructor
public class ItemNfsController {
	
	private final String EMPTY_MSG = "List is empty...";
	private final EntityManagerFactory entityManagerFactory;
	private final CommonsRepository commonsRepository;
	private final NfServRepository nfServRepository;
	private final NfServCommonsController nfServCommonsController; 
	private final ItemNfsRepository itemNfsRepository;
    
	private ItemNfsMapper itemNfsMapper = Mappers.getMapper(ItemNfsMapper.class);

	 @ApiOperation(value = "Get ItemNfs by Id")
	 @GetMapping("/find/{id}")
	 public ResponseEntity<Object> find(@RequestHeader("nfServId") Long nfServId, @PathVariable("id") Long id) {
		 
			CommonsValidation commonsValidation = this.createCommonsValidation();

		 try {
			 ItemNfs entity = itemNfsRepository.findById(nfServId, id, entityManagerFactory);
			 return ResponseEntity.ok().body(this.nfServCommonsController.mountDto(this.itemNfsMapper::toDto, entity));
		 } catch (Exception ex) {
			 return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		 }
	 }

	@ApiOperation(value = "Create a new ItemNfs record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("nfServId") Long nfServId,
			@Validated @RequestBody ItemNfsDto entityDto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			NfServ entity = commonsValidation.recuperarObjeto(nfServId, NfServ.class, entityManagerFactory);
			return this.mountEntity(entityDto, null, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@ApiOperation(value = "Updating a ItemNfs record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@PathVariable("id") Long id, @RequestHeader("nfServId") Long nfServId,
			@Validated @RequestBody ItemNfsDto entityDto) {
		
		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			NfServ entity = commonsValidation.recuperarObjeto(nfServId, NfServ.class, entityManagerFactory);
			return this.mountEntity(entityDto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@ApiOperation(value = "Deleting a ItemNfs record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("nfServId") Long nfServId, @PathVariable("id") Long id) {
		
		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			itemNfsRepository.delete(itemNfsRepository.findById(nfServId, id, entityManagerFactory));
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@ApiOperation(value = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader("nfServId") Long nfServId,
			@RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page,
			@PathVariable("length") int length) {

		try {
			Long count = itemNfsRepository.count(nfServId, null, entityManagerFactory);
			if (count > 0) {
				List<ItemNfs> list = itemNfsRepository.findAll(nfServId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(itemNfsMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@ApiOperation(value = "Get ItemNfs by nfServId")
	@GetMapping("/findByNfServId/{nfServId}/{page}/{length}")
	public ResponseEntity<Object> findByNfServId(@PathVariable("nfServId") Long nfServId,
			@RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page,
			@PathVariable("length") int length) {

		try {

			ItemNfsDto entityDto = ItemNfsDto.builder().nfServ(NfServDto.builder().id(nfServId).build()).build();
			Long count = itemNfsRepository.count(nfServId, entityDto, entityManagerFactory);
			if (count > 0) {
				List<ItemNfs> list = itemNfsRepository.findByFilters(nfServId, entityDto, entityManagerFactory,
						requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(itemNfsMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@ApiOperation(value = "Get ItemNfs by filters")
	@PostMapping("/buscarPorFiltros/{page}/{length}")
	public ResponseEntity<Object> buscarPorFiltros(@RequestHeader("nfServId") Long nfServId,
			@RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page,
			@PathVariable("length") int length, @RequestBody ItemNfsDto entityDto) {

		try {
			Long count = itemNfsRepository.count(nfServId, entityDto, entityManagerFactory);
			if (count > 0) {
				List<ItemNfs> list = itemNfsRepository.findByFilters(nfServId, entityDto, entityManagerFactory,
						requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(itemNfsMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(ItemNfsDto entityDto, Long id, NfServ nfServ, boolean update)
			throws ItemNfsException {

		try {
			
			ItemNfs entity = this.itemNfsMapper.toEntity(entityDto);
			HttpHeaders headers = new HttpHeaders();
			entity.setNfServ(nfServ);
			entity.setId(update ? id : null);
			entity = itemNfsRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return ResponseEntity.status(update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED).headers(headers)
					.body(itemNfsMapper.toDto(entity));
		} catch (NoResultException ex) {
	        return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new ItemNfsException(ex.getMessage(), ex);
		}
	}
	
	private CommonsValidation createCommonsValidation() {
		return CommonsValidation.builder()
				.commonsRepository(this.commonsRepository)
				.nfServRepository(this.nfServRepository)
				.itemNfsRepository(this.itemNfsRepository)
				.build();
	}
}
