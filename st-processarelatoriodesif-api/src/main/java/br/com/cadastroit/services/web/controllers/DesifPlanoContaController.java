package br.com.cadastroit.services.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cadastroit.services.api.domain.Filters;
import br.com.cadastroit.services.api.domain.HealthCheck;
import br.com.cadastroit.services.api.domain.NotificationEmail;
import br.com.cadastroit.services.exceptions.DesifPlanoContaException;
import br.com.cadastroit.services.mongodb.domain.CallRelatorioDesifApi;
import br.com.cadastroit.services.repository.impl.CallRelatorioDesifApiRepository;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@Validated
@RestController
@RequestMapping("/administracao/processarelatorio/desifplanoconta")
@AllArgsConstructor
public class DesifPlanoContaController {

	private CallRelatorioDesifApiRepository callRelatorioDesifRepository;

	@ApiOperation(value = "Get status from API")
	@GetMapping("/status")
	public ResponseEntity<Object> status(
			@RequestParam(name = "status", required = false, defaultValue = "UP") String status) {
		try {
			return new ResponseEntity<>(HealthCheck.builder().status(status).build(), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	@ApiOperation(value="Solicita uma lista de relatorios processados em um período)", response = CallRelatorioDesifApi.class)
	@GetMapping("/buscarPorFiltros/{page}/{length}")
	public ResponseEntity<Object> buscarPorFiltros(@RequestHeader("empresaId") Long empresaId,
			@RequestParam(required = true) Map<String, Object> requestParams, 
			@RequestBody(required = true) Filters filters, 
			@RequestParam(value = "filterOr", required = false) String filterOr,
            @RequestParam(value = "filterAnd", required = false) String filterAnd,
			@PathVariable("page") int page, @PathVariable("length") int length) {

		try {
		    
			AtomicReference<List<CallRelatorioDesifApi>> collection = new AtomicReference<>(new ArrayList<>());
			List<CallRelatorioDesifApi> list = this.callRelatorioDesifRepository
					.findCallRelatorioDesifApiByFilters(empresaId, requestParams, filters, filterOr, filterAnd, page, length);
			collection.get().addAll(list);
			return new ResponseEntity<Object>(collection.get(), HttpStatus.OK);
			
		} catch (DesifPlanoContaException ex) {
			return new ResponseEntity<Object>("Error on process request, [ERROR] = "+ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/sendNotificationEmail")
	public ResponseEntity<String> sendNotificationEmail(@ModelAttribute NotificationEmail notificationEmail) {
		boolean emailSent = false;
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending email.");
		}
}