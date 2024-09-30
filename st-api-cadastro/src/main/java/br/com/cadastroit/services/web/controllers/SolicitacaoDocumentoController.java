package br.com.cadastroit.services.web.controllers;

import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.http.ResponseEntity.ok;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.api.services.PedidoService;
import br.com.cadastroit.services.api.services.PessoaService;
import br.com.cadastroit.services.api.services.SolicitacaoDocumentoService;
import br.com.cadastroit.services.api.services.TarefaService;
import br.com.cadastroit.services.api.services.interfaces.ReportDepartamentoService;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Validated
@RestController
@RequestMapping("/administracao/solicitacaodocumento")
public class SolicitacaoDocumentoController extends ApiCadastroCommonsController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private ReportDepartamentoService reportDepartamentoService;
	
	@Autowired
	private SolicitacaoDocumentoService solicitacaoDocumentoService;
	
	public static final String X_SUGGESTED_FILENAME_HEADER = "X-SUGGESTED-FILENAME";
	
	@GetMapping("/maxId")
	public ResponseEntity<Object> maxId(@RequestHeader("tarefaId") Long tarefaId) {

		try {
			Long id = departamentoRepository.maxId(entityManagerFactory, tarefaId);
			return ResponseEntity.ok(id);
		} catch (Exception ex) {
			return ResponseEntity.badRequest().body((String.format(ex.getMessage())));
		}
	}
	
//	@GetMapping(value = "/arquivos-anexados/{id}")
//	public void obterArquivosAnexados(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
//
//		byte[] bytes = this.solicitacaoDocumentoService.obterArquivosAnexados(id);
//
//		response.setContentType("application/download");
//		response.setContentLength(bytes.length);
//		response.setHeader("Content-Disposition", "attachment;");
//		response.getOutputStream().write(bytes);
//	}


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
	
	public SolicitacaoDocumentoController(ObjectMapper mapperJson, PessoaRepository pessoaRepository, EmpresaRepository empresaRepository,
			TarefaRepository tarefaRepository, TarefaService tarefaService, DepartamentoRepository departamentoRepository,
			EnderecoRepository enderecoRepository, TelefoneRepository telefoneRepository, ParametroRepository parametroRepository,
			PessoaEmpresaRepository pessoaEmpresaRepository, PessoaService pessoaService, PedidoRepository pedidoRepository, PedidoService pedidoService) {

		super(mapperJson, pessoaRepository, empresaRepository, tarefaRepository, tarefaService, departamentoRepository, enderecoRepository,
				telefoneRepository, parametroRepository, pessoaEmpresaRepository, pessoaService, pedidoRepository, pedidoService);
	}
}
