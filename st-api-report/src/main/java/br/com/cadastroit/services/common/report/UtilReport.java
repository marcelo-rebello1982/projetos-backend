package br.com.cadastroit.services.common.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cadastroit.services.aws.AwsTempBucketClient;
import lombok.Builder;
import lombok.Data;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Builder
@Data
public class UtilReport {

	public String processaReportPdfS3(Connection conn, String dirJrxml, String dirJasper, Map<String, Object> param, String nome, boolean subReport)
			throws Exception {

		String urlPdf = "";
		byte[] reportByte = configurarProcessaReport(conn, dirJrxml, dirJasper, param, subReport);
		if (reportByte != null) {
			urlPdf = this.processarS3(nome, reportByte);
		}
		return resultJson(urlPdf);
	}

	public String processaReportZipS3(HashMap<String, byte[]> arcByteList) throws Exception {

		String urlPdf = "";
		if (!arcByteList.entrySet().isEmpty())
			urlPdf = this.processarS3Zip(arcByteList);
		return resultJson(urlPdf);
	}

	private String resultJson(String url) throws JsonProcessingException {

		Map<String, Object> map = new HashMap<>();
		if (url != null && !url.equals("")) {
			map.put("fileName", url);
			map.put("result", "success");
			map.put("extension", (url.contains(".pdf") ? "pdf" : "zip"));
		} else {
			map.put("fileName", "Nenhum documento encontrado para a busca realizada...");
			map.put("result", "erro");
			map.put("extension", "");
		}
		return new ObjectMapper().writeValueAsString(map);
	}

	private String processarS3(String nome, byte[] arcByte) throws Exception {

		AtomicReference<List<File>> files = new AtomicReference<>(new ArrayList<>());
		AtomicReference<String> fileName = new AtomicReference<>("");
		AtomicReference<AwsTempBucketClient> awsTempBucketClient = new AtomicReference<>();
		UtilAws utilAws = UtilAws.builder().build();
		String bucketName = "cstempnfs-pdf-files";
		try {
			String key = nome + ".pdf";

			if (awsTempBucketClient.get() == null)
				awsTempBucketClient.set(utilAws.buildAwsTempBuckClient(bucketName, key));
			File file = new File(key);
			Path path = Paths.get(key);

			if (!file.exists())
				file.createNewFile();
			Files.write(path, arcByte);

			utilAws.uploadAwsTempBuckClient(awsTempBucketClient.get(), bucketName, file);
			fileName.set(awsTempBucketClient.get().getTempURL());
			Files.delete(path);
		} catch (Exception ex) {
			throw new Exception("Falha na geração do relatório no S3 erro => " + ex.getMessage());
		}
		if (files.get().isEmpty())
			return fileName.get();
		else
			return utilAws.zipFiles(files.get(), fileName.get(), bucketName, awsTempBucketClient.get());
	}

	private String processarS3Zip(HashMap<String, byte[]> arcByteList) throws Exception {

		AtomicReference<String> msgErros = new AtomicReference<>();
		AtomicReference<List<File>> files = new AtomicReference<>(new ArrayList<>());
		AtomicReference<String> fileName = new AtomicReference<>("");
		AtomicReference<AwsTempBucketClient> awsTempBucketClient = new AtomicReference<>();
		UtilAws utilAws = UtilAws.builder().build();
		String bucketName = "cstempnfs-pdf-files";
		try {
			arcByteList.entrySet().stream().forEach(b -> {
				if (fileName.get().equals(""))
					fileName.set(System.currentTimeMillis() + ".zip");
				String key = fileName.get();
				String fileNamePart = b.getKey() + ".pdf";
				try {
					if (awsTempBucketClient.get() == null)
						awsTempBucketClient.set(utilAws.buildAwsTempBuckClient(bucketName, key));
					File filePart = new File(fileNamePart);
					if (!filePart.exists())
						filePart.createNewFile();
					Path path = Paths.get(fileNamePart);
					Files.write(path, b.getValue());
					files.get().add(filePart);
				} catch (IOException ex) {
					msgErros.set("Falha na geração do relatório em zip no S3 erro => " + ex.getMessage());
				}
			});
		} catch (Exception ex) {
			throw new Exception("Falha na geração do relatório no S3 erro => " + ex.getMessage());
		}
		if (msgErros.get() != null && !msgErros.get().isEmpty())
			throw new Exception(msgErros.get());
		if (files.get().isEmpty())
			return fileName.get();
		else
			return utilAws.zipFiles(files.get(), fileName.get(), bucketName, awsTempBucketClient.get());
	}

	public byte[] configurarProcessaReport(Connection conn, String dirJrxml, String dirJasper, Map<String, Object> param, boolean subReport)
			throws Exception {

		try {
			JasperReport report = null;
			byte[] bytesPDF = null;
			if (dirJrxml != null && !dirJrxml.equals("")) {
				report = getJasperCompiler(dirJrxml);
			} else if (dirJasper != null && !dirJasper.equals("")) {
				report = getJasperObject(dirJasper);
			} else {
				throw new JRException("Diretório do Report não informado ou inválido.");
			}
			if (subReport) {
				param.put("SUBREPORT_DIR", param.get("realPath") + File.separator);
			}
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			JasperPrint jasperPrint = fill(param, report, conn);
			ByteArrayOutputStream baosPdf = generatePDFReport(jasperPrint);
			if (baosPdf != null && baosPdf.size() > 0) {
				bytesPDF = baosPdf.toByteArray();
				baosPdf.close();
				return bytesPDF;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new Exception("Falha na geração do relatório erro => " + e.getMessage());
		}
	}

	private JasperReport getJasperObject(String jasperName) throws JRException {

		try {
			return (JasperReport) JRLoader.loadObject(new File(jasperName));
		} catch (JRException e) {
			throw new JRException(e.getMessage(), e);
		}
	}

	private JasperReport getJasperCompiler(String jrxmlName) throws JRException {

		try {
			return (JasperReport) JasperCompileManager.compileReport(jrxmlName);
		} catch (JRException e) {
			throw new JRException(e.getMessage(), e);
		}
	}

	private JasperPrint fill(Map<String, Object> parametros, Object jrPrincipal, Connection conn) throws JRException {

		return JasperFillManager.fillReport((JasperReport) jrPrincipal, parametros, conn);
	}

	private ByteArrayOutputStream generatePDFReport(JasperPrint jasperPrint) throws JRException {

		ByteArrayOutputStream baosPdf = new ByteArrayOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, baosPdf);
		return baosPdf;
	}
}
