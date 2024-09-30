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

import br.com.cadastroit.services.api.domain.FaturaNfs;
import br.com.cadastroit.services.api.domain.NfServ;
import br.com.cadastroit.services.exceptions.FaturaNfsException;
import br.com.cadastroit.services.repositories.CommonsRepository;
import br.com.cadastroit.services.repositories.FaturaNfsRepository;
import br.com.cadastroit.services.repositories.NfServRepository;
import br.com.cadastroit.services.web.controller.commons.CommonsValidation;
import br.com.cadastroit.services.web.controller.commons.NfServCommonsController;
import br.com.cadastroit.services.web.dto.FaturaNfsDto;
import br.com.cadastroit.services.web.mapper.FaturaNfsMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/administracao/nfserv/faturanfs")
@RequiredArgsConstructor
public class FaturaNfsController {
	
	private final String EMPTY_MSG = "List is empty...";
	private final EntityManagerFactory entityManagerFactory;
	private final CommonsRepository commonsRepository;
	private final NfServRepository nfServRepository;
	private final NfServCommonsController nfServCommonsController; 
	private final FaturaNfsRepository faturaNfsRepository;
    
	private FaturaNfsMapper faturaNfsMapper = Mappers.getMapper(FaturaNfsMapper.class);
	
	@ApiOperation(value = "Get maxId FaturaNfs")
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("nfServId") Long nfServId) {
		try {
			Long id = faturaNfsRepository.maxId(entityManagerFactory, nfServId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}

	@ApiOperation(value = "Get FaturaNfs by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@RequestHeader("nfServId") Long nfServId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			FaturaNfs entity = faturaNfsRepository.findById(nfServId, id, entityManagerFactory);
			return ResponseEntity.ok().body(this.nfServCommonsController.mountDto(faturaNfsMapper::toDto, entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@ApiOperation(value = "Create a new FaturaNfs record")
	@PostMapping("/create")
	public ResponseEntity<Object> handlePost(@RequestHeader("nfServId") Long nfServId,
			@Validated @RequestBody FaturaNfsDto entityDto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			NfServ entity = commonsValidation.recuperarObjeto(nfServId, NfServ.class, entityManagerFactory);
			return this.mountEntity(entityDto, null, entity, false);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@ApiOperation(value = "Updating a FaturaNfs record")
	@PutMapping("/update/{id}")
	public ResponseEntity<Object> handleUpdate(@PathVariable("id") Long id, @RequestHeader("nfServId") Long nfServId,
			@Validated @RequestBody FaturaNfsDto entityDto) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			NfServ entity = commonsValidation.recuperarObjeto(nfServId, NfServ.class, entityManagerFactory);
			return this.mountEntity(entityDto, id, entity, true);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}

	@ApiOperation(value = "Deleting a FaturaNfs record")
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Object> delete(@RequestHeader("nfServId") Long nfServId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			faturaNfsRepository.delete(faturaNfsRepository.findById(nfServId, id, entityManagerFactory));
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
			Long count = faturaNfsRepository.count(nfServId, null, entityManagerFactory);
			if (count > 0) {
				List<FaturaNfs> list = faturaNfsRepository.findAll(nfServId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(faturaNfsMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@ApiOperation(value = "Get FaturaNfs by filters")
	@PostMapping("/buscarPorFiltros/{page}/{length}")
	public ResponseEntity<Object> buscarPorFiltros(@RequestHeader("nfServId") Long nfServId,
			@RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page,
			@PathVariable("length") int length, @RequestBody FaturaNfsDto entityDto) {

		try {
			Long count = faturaNfsRepository.count(nfServId, entityDto, entityManagerFactory);
			if (count > 0) {
				List<FaturaNfs> list = faturaNfsRepository.findByFilters(nfServId, entityDto, entityManagerFactory,
						requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(faturaNfsMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private ResponseEntity<Object> mountEntity(FaturaNfsDto entityDto, Long id, NfServ nfServ, boolean update)
			throws FaturaNfsException {

		try {
			
			FaturaNfs entity = this.faturaNfsMapper.toEntity(entityDto);
			HttpHeaders headers = new HttpHeaders();
			entity.setNfServ(nfServ);
			entity.setId(update ? id : null);
			entity = faturaNfsRepository.save(entity);
			headers.add("Location", "/find/" + entity.getId());
			return ResponseEntity.status(update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED).headers(headers)
					.body(faturaNfsMapper.toDto(entity));
		} catch (NoResultException ex) {
	        return ResponseEntity.badRequest().body(ex.getMessage());
		} catch (Exception ex) {
			throw new FaturaNfsException(ex.getMessage(), ex);
		}
	}
	
	private CommonsValidation createCommonsValidation() {
		return CommonsValidation.builder()
				.commonsRepository(this.commonsRepository)
				.nfServRepository(this.nfServRepository)
				.faturaNfsRepository(this.faturaNfsRepository)
				.build();
	}
}
