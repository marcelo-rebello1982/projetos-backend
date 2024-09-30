package br.com.cadastroit.services.api.services.interfaces;
public interface ReportDepartamentoService {
	
	public byte[] generateReportPdf(String nameReport);
	
	public byte[] generateDepartamentoReportCsv();

}