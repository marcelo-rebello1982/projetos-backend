//package br.com.cadastroit.services.web.controllers;
//
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.NoResultException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import br.com.cadastroit.services.api.domain.Empresa;
//import br.com.cadastroit.services.exceptions.PessoaException;
//import br.com.cadastroit.services.repositories.EmpresaRepository;
//import br.com.cadastroit.services.repositories.ItemRepository;
//import br.com.cadastroit.services.web.controllers.commons.ApiProdutoCommonsController;
//import br.com.cadastroit.services.web.controllers.commons.CommonsValidation;
//import br.com.cadastroit.services.web.dto.EmpresaDTO;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
//@Validated
//@RestController
//@RequestMapping("/administracao/empresa")
//public class EmpresaController extends ApiProdutoCommonsController {
//
//	@Autowired
//	private EntityManagerFactory entityManagerFactory;
//
//	@Tag(name = "Empresa", description = "Get maxId From Empresa")
//	@GetMapping("/maxId")
//	public ResponseEntity<Object> maxId() {
//
//		try {
//			Long id = empresaRepository.maxId(entityManagerFactory);
//			return ResponseEntity.ok(id);
//		} catch (Exception ex) {
//			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//
//	@Tag(name = "Empresa", description = "Get Empresa by Id")
//	@GetMapping("/find/{id}")
//	public ResponseEntity<Object> find(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) {
//
//		CommonsValidation commonsValidation = this.createCommonsValidation();
//
//		try {
//			Empresa entity = empresaRepository.findById(id, entityManagerFactory);
//			return ResponseEntity.ok().body(mountObject(empresaMapper::toDto, entity));
//		} catch (Exception ex) {
//			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
//		}
//	}
//
//	public ResponseEntity<Object> handlePost(@RequestHeader("empresaId") Long empresaId, @Validated @RequestBody EmpresaDTO dto) {
//
//		CommonsValidation commonsValidation = this.createCommonsValidation();
//
//		try {
//			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
//			return mountEntity(dto, null, entity, false);
//		} catch (Exception ex) {
//			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
//		}
//	}
//
//	@Tag(name = "Empresa", description = "Updating a Pessoa record")
//	@PutMapping("/update/{id}")
//	public ResponseEntity<Object> handleUpdate(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id, @Validated @RequestBody EmpresaDTO dto) {
//
//		CommonsValidation commonsValidation = this.createCommonsValidation();
//
//		try {
//			Empresa entity = commonsValidation.recuperarEmpresa(empresaId, entityManagerFactory);
//			return this.mountEntity(dto, id, entity, true);
//		} catch (Exception ex) {
//			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
//		}
//	}
//
//	@Tag(name = "Empresa", description = "Deleting a Empresa record")
//	@DeleteMapping("/delete/{id}")
//	public ResponseEntity<Object> delete(@RequestHeader("empresaId") Long empresaId, @PathVariable("id") Long id) throws PessoaException {
//
//		CommonsValidation commonsValidation = this.createCommonsValidation();
//
//		try {
//			empresaRepository.delete(empresaRepository.findById(empresaId, entityManagerFactory));
//			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//		} catch (Exception ex) {
//			return ResponseEntity.badRequest().body(commonsValidation.createResponseTemplate(String.format(ex.getMessage())));
//		}
//	}
//
//	private ResponseEntity<Object> mountEntity(EmpresaDTO dto, Long id, Empresa empresa, boolean update) throws PessoaException {
//
//		try {
//
//			CommonsValidation commonsValidation = this.createCommonsValidation();
//
//			Empresa entity = empresaMapper.toEntity(dto);
//
//			HttpHeaders headers = new HttpHeaders();
//			entity.setId(update ? id : null);
//			entity = empresaRepository.save(entity);
//			headers.add("Location", "/find/" + entity.getId());
//			return new ResponseEntity<>(entity, headers, update ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
//		} catch (NoResultException ex) {
//			return ResponseEntity.badRequest().body(ex.getMessage());
//		} catch (Exception ex) {
//			throw new PessoaException(ex.getMessage(), ex);
//		}
//	}
//
//	private CommonsValidation createCommonsValidation() {
//
//		return CommonsValidation.builder().empresaRepository(new EmpresaRepository()).itemRepository(new ItemRepository()).build();
//
//	}
//
//	public EmpresaController(ObjectMapper mapperJson, EmpresaRepository empresaRepository, ItemRepository itemRepository) {
//
//		super(mapperJson, empresaRepository, itemRepository);
//	}
//}
