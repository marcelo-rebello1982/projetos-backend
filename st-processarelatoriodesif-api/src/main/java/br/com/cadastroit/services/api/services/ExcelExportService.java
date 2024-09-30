package br.com.cadastroit.services.api.services;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.cadastroit.services.api.domain.DesifPlanoConta;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ExcelExportService {

	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private List<DesifPlanoConta> list;

	public ExcelExportService(List<DesifPlanoConta> list) {
		this.list = list;
		workbook = new XSSFWorkbook();
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	public void exportDataToExcel(HttpServletResponse response, String title) throws IOException {
		createHeaderRow(title);
		writeData();
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

	private void createHeaderRow(String title) {
		sheet = workbook.createSheet(title);
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();

		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(20);

		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);

		createCell(row, 0, "RELATÓRIO DESIF - TITULO DO RELATORIO - XXXXX", style);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
		font.setFontHeightInPoints((short) 10);

		row = sheet.createRow(1);
		font.setBold(true);
		font.setFontHeight(10);
		style.setFont(font);
		createCell(row, 0, "COD CTA", style);
		createCell(row, 1, "DES-MISTA", style);
		createCell(row, 2, "NOME", style);
		createCell(row, 3, "DESCR_CTA", style);
		createCell(row, 4, "COD_CTA_SUP", style);
		createCell(row, 5, "CONTA_COSIF", style);
		createCell(row, 6, "COD_TRIB_MUN", style);
		createCell(row, 7, "CONTA_REDUZIDA", style);
		createCell(row, 8, "STATUS", style);
	}

	private void writeData() {
		int rowCount = 2;
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(10);
		style.setFont(font);

		for (DesifPlanoConta entity : list) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;
			createCell(row, columnCount++, entity.getCodCta(), style);
			createCell(row, columnCount++, entity.getDesMista().doubleValue(), style);
			createCell(row, columnCount++, entity.getNome(), style);
			createCell(row, columnCount++, entity.getDescrCta(), style);
			createCell(row, columnCount++, entity.getCodCtaSup(), style);
			createCell(row, columnCount++, entity.getDesifCadPcCosIf().getCodCta().concat("-") + entity.getDesifCadPcCosIf().getNomeConta(),style);
			//createCell(row, columnCount++, "ACERTAR CAMPO",style);
			createCell(row, columnCount++, entity.getContaReduzida(), style);
			createCell(row, columnCount++, entity.getDmSituacao(), style);
		}
	}
}