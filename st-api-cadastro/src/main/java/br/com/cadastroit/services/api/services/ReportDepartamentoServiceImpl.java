package br.com.cadastroit.services.api.services;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cadastroit.services.api.domain.Departamento;
import br.com.cadastroit.services.api.services.interfaces.ReportDepartamentoService;
import br.com.cadastroit.services.repositories.impl.DepartamentoRepositoryImpl;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class ReportDepartamentoServiceImpl implements ReportDepartamentoService {
	
	@Autowired
	private DepartamentoRepositoryImpl departamentoRepositoryImpl;
	
	@Autowired
	DataSource dataSource;

	@Override
	public byte[] generateReportPdf(String nameReport) {

		try {
			String fileReport = String.format("/report/src/%s.jasper", nameReport);
			JasperReport compile = (JasperReport) JRLoader.loadObject(this.getClass().getResourceAsStream(fileReport));
			try (Connection connection = dataSource.getConnection()) {
				Map<String, Object> parameters = new LinkedHashMap<>();
				JasperPrint jasperPrint = JasperFillManager.fillReport(compile, parameters, connection);
				return JasperExportManager.exportReportToPdf(jasperPrint);
			} catch (SQLException sqle) {
				throw new RuntimeException("Report SQL Error", sqle);
			}
		} catch (JRException jrpe) {
			throw new RuntimeException("Report Error", jrpe);
		}
	}

	@Override
	public byte[] generateDepartamentoReportCsv() {

		StringBuilder str = new StringBuilder();
		List<Departamento> departamentos = departamentoRepositoryImpl.findAll();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		str.append("Name,E-mail,BirthDate");

		for (Departamento departamento : departamentos) {

			str.append(System.lineSeparator());
			str.append(departamento.getId()).append(",").append(departamento.getDescr()).append(",");

		}

		Charset charset = StandardCharsets.UTF_8;
		return str.toString().getBytes(charset);
	}
}