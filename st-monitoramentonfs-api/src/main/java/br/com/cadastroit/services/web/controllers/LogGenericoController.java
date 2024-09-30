package br.com.cadastroit.services.web.controllers;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cadastroit.services.api.domain.LogGenerico;
import br.com.cadastroit.services.repositories.CommonsRepository;
import br.com.cadastroit.services.repositories.LogGenericoRepository;
import br.com.cadastroit.services.repositories.NfServRepository;
import br.com.cadastroit.services.web.controller.commons.CommonsValidation;
import br.com.cadastroit.services.web.controller.commons.NfServCommonsController;
import br.com.cadastroit.services.web.dto.LogGenericoDto;
import br.com.cadastroit.services.web.mapper.LogGenericoMapper;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/administracao/nfserv/loggenerico")
@RequiredArgsConstructor
public class LogGenericoController {
	
	private final String EMPTY_MSG = "List is empty...";
	private final EntityManagerFactory entityManagerFactory;
	private final CommonsRepository commonsRepository;
	private final NfServRepository nfServRepository;
	private final NfServCommonsController nfServCommonsController; 
	private final LogGenericoRepository logGenericoRepository;
	
    private final LogGenericoMapper LogGenericoMapper = Mappers.getMapper(LogGenericoMapper.class);
    
	@ApiOperation(value = "Get LogGenerico by Id")
	@GetMapping("/find/{id}")
	public ResponseEntity<Object> find(@RequestHeader( value = "csfTipoLogId", required = false) Long csfTipoLogId, @PathVariable("id") Long id) {

		CommonsValidation commonsValidation = this.createCommonsValidation();

		try {
			LogGenerico entity = this.logGenericoRepository.findById(csfTipoLogId, id, entityManagerFactory);
			return ResponseEntity.ok().body(this.nfServCommonsController.mountDto(LogGenericoMapper::toDto, entity));
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
		}
	}
	
	@ApiOperation(value = "Get all records using pagination mechanism")
	@GetMapping("/all/{page}/{length}")
	public ResponseEntity<Object> all(@RequestHeader(value = "csfTipoLogId" , required = false) Long csfTipoLogId,
			@RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page,
			@PathVariable("length") int length) {

		try {
			Long count = logGenericoRepository.count(csfTipoLogId, null, entityManagerFactory);
			if (count > 0) {
				List<LogGenerico> list = logGenericoRepository.findAll(csfTipoLogId, entityManagerFactory, requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(LogGenericoMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@ApiOperation(value = "Get LogGenerico by filters")
	@PostMapping("/buscarPorFiltros/{page}/{length}")
	public ResponseEntity<Object> buscarPorFiltros(@RequestHeader(value = "csfTipoLogId" , required = false) Long csfTipoLogId,
			@RequestParam(required = false) Map<String, Object> requestParams, @PathVariable("page") int page,
			@PathVariable("length") int length, @RequestBody LogGenericoDto entityDto) {

		try {
			Long count = logGenericoRepository.count(csfTipoLogId, entityDto, entityManagerFactory);
			if (count > 0) {
				List<LogGenerico> list = logGenericoRepository.findByFilters(csfTipoLogId, entityDto, entityManagerFactory,
						requestParams, page, length);
				HttpHeaders headers = this.nfServCommonsController.httpHeaders(count.toString());
				return ResponseEntity.ok().headers(headers).body(this.nfServCommonsController.mountListDto(LogGenericoMapper::toDto, list));
			}
			return ResponseEntity.ok().headers(this.nfServCommonsController.httpHeaders(count.toString())).body(EMPTY_MSG);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	private CommonsValidation createCommonsValidation() {
		return CommonsValidation.builder()
				.commonsRepository(this.commonsRepository)
				.nfServRepository(this.nfServRepository)
				.logGenericoRepository(this.logGenericoRepository)
				.build();
	}
} 
 
