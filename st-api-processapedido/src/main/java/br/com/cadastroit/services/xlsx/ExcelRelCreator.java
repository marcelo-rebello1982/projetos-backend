package br.com.cadastroit.services.xlsx;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.cadastroit.services.api.domain.Pedido;
import br.com.cadastroit.services.exceptions.PedidoException;

@Service
@Component
public class ExcelRelCreator {

	private int rowCount = 1;

	private XSSFSheet sheet;
	private XSSFWorkbook workbook;
	private List<Pedido> list;

	private String[] headers = new String[] { "CÓDIGO CONTA", "DES MISTA", "NOME CONTA", "DESCRIÇÃO CONTA",
			"COD_CTA_SUP", "CONTA_COSIF", "CODIGO_TRIB_MUN", "CONTA_REDUZIDA", "STATUS" };

	public ExcelRelCreator(List<Pedido> list) {
		this.list = list;
		workbook = new XSSFWorkbook();
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof BigDecimal) {
			cell.setCellValue((String) value.toString());
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Date) {
			Date date = (Date) value;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
			cell.setCellValue((sdf.format(date).toString()));
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	public File writeData(List<Pedido> list, Long pessoaId, String currentDateTime) throws IOException {

		try {
			list = this.list;
			createHeader();
			writeData(list);
			Path currentDirectory = FileSystems.getDefault().getPath("").toAbsolutePath();
			File file = new File("RelatorioPedido_".concat(pessoaId.toString() + "_").concat(currentDateTime) + ".xlsx");
			FileOutputStream out = new FileOutputStream(currentDirectory.toString() + File.separator + file);
			workbook.write(out);
			return file.getAbsoluteFile();
		} catch (Exception ex) {
			throw new PedidoException(String.format(ex.getMessage()));
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}

	private void createHeader() {

		sheet = workbook.createSheet("RELATÓRIO PEDIDO");
		sheet.setColumnWidth(3, 25 * 256);
		sheet.setDefaultColumnWidth(15);
		sheet.setDefaultRowHeight((short) 400);
		Row row = sheet.createRow(0);
		sheet.setDefaultColumnWidth((short) 12);

		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Calibri");
		font.setBold(true);
		font.setFontHeightInPoints((short) 12);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		style.setFont(font);

		IntStream.range(0, headers.length).forEach(index -> {
			HSSFRichTextString value = new HSSFRichTextString(headers[index]);
			createCell(row, index, value.toString(), style);
		});
	}

	private void writeData(List<Pedido> list) {

		XSSFFont font = workbook.createFont();
		XSSFCellStyle style = workbook.createCellStyle();
		font.setFontHeight(12);
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());

		list.stream().forEach((object) -> {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			createCell(row, columnCount++, object.getId(), style);
			// createCell(row, columnCount++, object.getPessoa(), style); ESTA CHEGANDO UM ID APENAS, AJUSTAR DEPOIS
			createCell(row, columnCount++, object.getStatus().name(), style);
			// createCell(row, columnCount++, object.getgetDescrCta(), style);
			// createCell(row, columnCount++, object.getCodCtaSup(), style);
			// createCell(row, columnCount++, object.getDesifCadPcCosIf().getCodCta().concat(" - " + object.getDesifCadPcCosIf().getNomeConta()), style);
			// createCell(row, columnCount++, object.getDesifCadCodTribMun() != null ? object.getDesifCadCodTribMun().getCodTribMun() : null, style);
			// createCell(row, columnCount++, object.getContaReduzida(), style);
			// createCell(row, columnCount++, this.converterCampoStatus(object.getId()), style);
		});
	}

}
