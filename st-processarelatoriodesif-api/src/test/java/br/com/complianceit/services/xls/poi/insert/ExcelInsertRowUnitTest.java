package br.com.complianceit.services.xls.poi.insert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExcelInsertRowUnitTest {
	private static final String FILE_NAME = "br/com/complianceit/services/xls/poi/test.xlsx";
	private static final String NEW_FILE_NAME = "br/com/complianceit/services/xls/poi/new_test.xlsx";
	private String fileLocation;

	@BeforeAll
	public void setup() throws URISyntaxException {
		fileLocation = Paths.get(ClassLoader.getSystemResource(FILE_NAME).toURI()).toString();
	}

	@Test
	public void givenWorkbook_whenInsertRowBetween_thenRowCreated() throws IOException {
		int startRow = 2;
		int rowNumber = 1;
		Workbook workbook = new XSSFWorkbook(fileLocation);
		Sheet sheet = workbook.getSheetAt(0);
		int lastRow = sheet.getLastRowNum();
		if (lastRow < startRow) {
			sheet.createRow(startRow);
		}
		sheet.shiftRows(startRow, lastRow, rowNumber, true, true);
		sheet.createRow(startRow);
		FileOutputStream outputStream = new FileOutputStream(NEW_FILE_NAME);
		workbook.write(outputStream);
		File file = new File(NEW_FILE_NAME);

		final int expectedRowResult = 5;
		Assertions.assertEquals(expectedRowResult, workbook.getSheetAt(0).getLastRowNum());

		outputStream.close();
		file.delete();
		workbook.close();
	}

}