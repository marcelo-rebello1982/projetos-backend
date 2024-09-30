package br.com.cadastroit.services.api.services;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cadastroit.services.repositories.impl.DepartamentoRepositoryImpl;
import br.com.cadastroit.services.repositories.impl.SolicitacaoDocumentoRepositoryImpl;

@Service
public class SolicitacaoDocumentoService {

	@Autowired
	private DepartamentoRepositoryImpl departamentoRepositoryImpl;
	
	@Autowired
	private SolicitacaoDocumentoRepositoryImpl solicitacaoDocumentoRepositoryImpl;
	
	@Autowired
	DataSource dataSource;
	
	
//	public byte[] obterArquivosAnexados(Long id) {
//
//		SolicitacaoDocumento entity = this.solicitacaoDocumentoRepositoryImpl.findById(id).get();
//
//		List<SolicitacaoDocumentoArquivoAnexado> arquivosAnexados = entity.getArquivosAnexados();
//		if (CollectionUtils.isEmpty(arquivosAnexados))
//			throw new BusinessException("Solicitação de pagamento não possui arquivos anexados.");
//
//		List<File> files = arquivosAnexados.stream().map(arquivoAnexado -> {
//
//			String fileUrl = ""; // this.storage.download(arquivoAnexado.getNomeSalvo());
//
//			byte[] fileAsBytes;
//
//			try {
//				fileAsBytes = FileUtils.getBytesFromUrl(fileUrl);
//			} catch (IOException e) {
//				throw new BusinessException("Ocorreu um erro ao obter os dados do arquivo anexado.");
//			}
//
//			return FileUtils.bytesToFile(fileAsBytes, arquivoAnexado.getNomeOriginal());
//
//		}).collect(Collectors.toList());
//
//		try {
//			return FileUtils.gerarZip(files);
//		} catch (IOException e) {
//			throw new BusinessException("Ocorreu um erro ao obter os arquivos anexados.");
//		}
//	}
//
//	public byte[] generateReportPdf(String nameReport) {
//
//		try {
//			String fileReport = String.format("/report/src/%s.jasper", nameReport);
//			JasperReport compile = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream(fileReport));
//			try (Connection connection = dataSource.getConnection()) {
//				Map<String, Object> parameters = new LinkedHashMap<>();
//				JasperPrint jasperPrint = JasperFillManager.fillReport(compile, parameters, connection);
//				return JasperExportManager.exportReportToPdf(jasperPrint);
//			} catch (SQLException sqle) {
//				throw new RuntimeException("Report SQL Error", sqle);
//			}
//		} catch (JRException jrpe) {
//			throw new RuntimeException("Report Error", jrpe);
//		}
//	}
//
//	public byte[] generateDepartamentoReportCsv() {
//
//		StringBuilder str = new StringBuilder();
//		List<Departamento> departamentos = departamentoRepositoryImpl.findAll();
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//		str.append("Name,E-mail,BirthDate");
//
//		for (Departamento departamento : departamentos) {
//
//			str.append(System.lineSeparator());
//			str.append(departamento.getId()).append(",").append(departamento.getDescr()).append(",");
//
//		}
//
//		Charset charset = StandardCharsets.UTF_8;
//		return str.toString().getBytes(charset);
//	}
}