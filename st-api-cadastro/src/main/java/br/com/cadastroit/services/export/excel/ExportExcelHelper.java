package br.com.cadastroit.services.export.excel;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import br.com.cadastroit.services.export.ExportBaseHelper;
import br.com.cadastroit.services.utils.DateUtils;
import br.com.cadastroit.services.utils.StringUtils;

public abstract class ExportExcelHelper extends ExportBaseHelper {

	private final static Logger LOG = LoggerFactory.getLogger(ExportExcelHelper.class);

	private final static int ROWS_IN_MEMORY = 1000;
	private final static String DEFAULT_NAME = "Planilha";

	public final static String NEW_LINE = System.getProperty("line.separator");
	
	
	/**
	 * Cria planilha com os dados de {@code dto}. Utiliza anotações {@link ExportExcelSheet} e {@link ExportExcelColumn} para
	 * obter headers e dados para setar nas células.<br>
	 * Este método é utilizado quando existir múltiplas listas de objetos para exportação e terá como resultado um arquivo (.xls,
	 * .xlsx) com <strong>uma planilha para cada lista de objetos</strong>.
	 * 
	 * 
	 * <p>
	 * Exemplo:<br>
	 * O DTO definido da forma abaixo terá como resultado:<br>
	 * - um arquivo contendo duas planilhas:
	 * <ul>
	 * <li>Uma planilha Teste1 com os dados da lista planilha1. A planilha será criada com duas colunas: "Nome do cliente" e
	 * "Idade do cliente" pois respeita a ordem definida na classe XyzDTO (ver exemplo de definição da classe XyzDTO abaixo).</li>
	 * <li>Uma planilha Teste2 com os dados da lista planilha3.</li>
	 * </ul>
	 * - A lista planilha2 será ignorada porque o campo não utiliza a anotação {@link ExportExcelSheet}.
	 * </p>
	 * 
	 * <p>
	 * Definição da classe:
	 * </p>
	 *
	 * <pre>
	 * class AbcDTO {
	 * 
	 * 	{@literal @}{@link ExportExcelSheet}("Teste1")
	 *	private List&lt;XyzDTO&gt; planilha1;
	 *
	 *	private List&lt;ZyxDTO&gt; planilha2;
	 *
	 *	{@literal @}{@link ExportExcelSheet}("Teste2")
	 *	private List&lt;XptDTO&gt; planilha3;
	 *
	 *	[getters e setters]
	 * 
	 * }
	 * </pre>
	 * 
	 * <p>
	 * Definição do conteúdo da planilha:
	 * </p>
	 * 
	 * <pre>
	 * class XyzDTO {
	 * 
	 * 	{@literal @}{@link ExportExcelColumn}(name = "Nome do cliente")
	 *	private String nome;
	 * 
	 * 	{@literal @}{@link ExportExcelColumn}(name = "Idade do cliente")
	 *	private int idade;
	 *
	 *	[getters e setters]
	 * 
	 * }
	 * </pre>
	 * 
	 * @param <T> Tipo genérico para exportação.
	 * @param dto Objeto para exportação.
	 * @return Conteúdo do arquivo gerado.
	 */
	public static <T> byte[] generate(T dto) {

		Class<?> clazz = dto.getClass();

		try (SXSSFWorkbook workbook = new SXSSFWorkbook(ROWS_IN_MEMORY)) {

			for (Field field : clazz.getDeclaredFields()) {

				field.setAccessible(true);

				try {

					Object value = field.get(dto);

					if (value instanceof List) {

						List<?> dtos = (List<?>) value;

						if (CollectionUtils.isEmpty(dtos))
							continue;

						Class<?> headerType = dtos.get(0).getClass();

						ExportExcelSheet excelSheet = getSheetName(field);
						String sheetName = excelSheet != null && StringUtils.isNotBlank(excelSheet.sheetName()) ? excelSheet.sheetName()
								: DEFAULT_NAME;
						boolean autoSizeColumn = excelSheet != null && excelSheet.autoSizeColumn();
						boolean ignoreNullValues = excelSheet != null && excelSheet.ignoreNullValues();

						List<String> columnHeaders = getColumnHeaders(headerType);

						if (CollectionUtils.isEmpty(columnHeaders))
							continue;

						createSheet(workbook, sheetName, columnHeaders, dtos, autoSizeColumn, ignoreNullValues);

					}

				} catch (IllegalArgumentException | IllegalAccessException e) {

					e.printStackTrace();

				}

			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);

			return baos.toByteArray();

		} catch (Exception e) {
			throw new ExportExcelException("Ocorreu um erro ao gerar a exportação.", e);
		}

	}
	
	
	/**
	 * Cria planilha com os dados de {@code dtos}. Utiliza anotações {@link ExportExcelSheet} e {@link ExportExcelColumn} para
	 * obter headers e dados para setar nas células.<br>
	 * Este método é utilizado quando existir apenas uma lista de objetos para exportação e terá como resultado um arquivo (.xls,
	 * .xlsx) com uma planilha.
	 * 
	 * 
	 * <p>
	 * Exemplo:<br>
	 * O DTO definido da forma abaixo terá como resultado um arquivo contendo uma planilha chamada "Terceiros".<br>
	 * A planilha será criada com duas colunas: "Nome do cliente" e "Idade do cliente" pois respeita a ordem definida na classe
	 * XyzDTO.
	 * 
	 * <p>
	 * Definição do conteúdo da planilha:
	 * </p>
	 * 
	 * <pre>
	 * {@literal @}{@link ExportExcelSheet}("Terceiros")
	 * class XyzDTO {
	 * 
	 * 	{@literal @}{@link ExportExcelColumn}(name = "Nome do cliente")
	 *	private String nome;
	 * 
	 * 	{@literal @}{@link ExportExcelColumn}(name = "Idade do cliente")
	 *	private int idade;
	 *
	 *	[getters e setters]
	 * 
	 * }
	 * </pre>
	 * 
	 * 
	 * @param dtos Lista de objetos para criação da planilha.
	 * @return Conteúdo da planilha gerada em {@code byte[]} para download.
	 * @throws ExportExcelException Caso alguma configuração não foi utilizada ou algum problema de tipo não suportado for
	 *             encontrado.
	 */
	public static byte[] generate(List<?> dtos) throws ExportExcelException {

		if (CollectionUtils.isEmpty(dtos))
			return null;

		Class<?> headerType = dtos.get(0).getClass();
		ExportExcelSheet excelSheet = getExportExcelSheetAnnotation(headerType);
		String sheetName = excelSheet != null && StringUtils.isNotBlank(excelSheet.sheetName()) ? excelSheet.sheetName() : DEFAULT_NAME;
		boolean autoSizeColumn = excelSheet != null && excelSheet.autoSizeColumn();
		boolean ignoreNullValues = excelSheet != null && excelSheet.ignoreNullValues();

		List<String> columnHeaders = getColumnHeaders(headerType);

		if (CollectionUtils.isEmpty(columnHeaders))
			throw new ExportExcelException(String.format(
					"A classe \"%s\" não está configurada para exportação. Utilize as anotações nos campos que deseja exportar para o arquivo excel.",
					headerType.toString()));

		try (SXSSFWorkbook workbook = new SXSSFWorkbook(ROWS_IN_MEMORY)) {

			createSheet(workbook, sheetName, columnHeaders, dtos, autoSizeColumn, ignoreNullValues);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);

			return baos.toByteArray();

		} catch (Exception e) {
			throw new ExportExcelException("Ocorreu um erro ao gerar a exportação.", e);
		}

	}

	public static byte[] generateSampleFile(Class<?> headerType) {

		ExportExcelSheet excelSheet = getExportExcelSheetAnnotation(headerType);
		String sheetName = excelSheet != null && StringUtils.isNotBlank(excelSheet.sheetName()) ? excelSheet.sheetName() : DEFAULT_NAME;

		List<String> columnHeaders = getColumnHeaders(headerType);

		try (SXSSFWorkbook workbook = new SXSSFWorkbook(ROWS_IN_MEMORY)) {

			SXSSFSheet sheet = workbook.createSheet(sheetName);
			int lineIndex = 0;
			int columnHeaderIndex = 0;
			int columnIndex = 0;

			short textFormat = createTextDataFormat(workbook);

			Row header = sheet.createRow(lineIndex++);
			Row row = sheet.createRow(lineIndex++);

			for (String columnName : columnHeaders) {
				Cell cellHeader = createCell(header, columnHeaderIndex++);
				cellHeader.setCellValue(columnName);

				Cell cell = createCell(row, columnIndex++);
				setCellStyle(cell, false, textFormat);
				setCellValue(cell, StringUtils.EMPTY);
			}

			sheet.trackAllColumnsForAutoSizing();

			for (int i = 0; i < columnHeaders.size(); i++) {
				sheet.autoSizeColumn(i);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);

			return baos.toByteArray();

		} catch (Exception e) {
			throw new ExportExcelException("Ocorreu um erro ao gerar a exportação.", e);
		}

	}

	private static short createTextDataFormat(SXSSFWorkbook workbook) {

		DataFormat dataFormat = workbook.createDataFormat();
		return dataFormat.getFormat("@");
	}

	private static List<String> getColumnHeaders(Class<?> headerType) {

		List<ExportExcelColumn> columns = getFieldsMethodsRecursive(headerType).filter(ao -> ao.isAnnotationPresent(ExportExcelColumn.class))
				.map(ao -> {
					ao.setAccessible(true);
					return ao.getAnnotation(ExportExcelColumn.class);
				})
				.collect(Collectors.toList());

		if (columns.stream().anyMatch(c -> c.order() > 0)) {
			columns = columns.stream().sorted((a, b) -> Integer.compare(a.order(), b.order())).collect(Collectors.toList());
		}

		return columns.stream().map(c -> c.name()).collect(Collectors.toList());

	}

	private static void createSheet(SXSSFWorkbook workbook, String sheetName, List<String> columnHeaders, List<?> dtos, boolean autoSizeColumn, boolean ignoreNullValues)
			throws ExportExcelException {

		Map<Integer, List<ExportExcelColumnDTO>> lines = new HashMap<>();
		int mapIndex = 0;

		for (Object dto : dtos) {

			Class<?> clazzLine = dto.getClass();

			List<ExportExcelColumnDTO> exportColumns = getFieldsMethodsRecursive(clazzLine).filter(ao -> {
				ao.setAccessible(true);
				return ao.isAnnotationPresent(ExportExcelColumn.class);
			}).map(ao -> {

				ao.setAccessible(true);
				ExportExcelColumn annotation = ao.getAnnotation(ExportExcelColumn.class);

				Object value = null;
				String dateFormat = annotation.dateFormat();
				int order = annotation.order();
				boolean wrapText = annotation.wrapText();

				if (ao instanceof Field) {

					Field field = (Field) ao;

					try {

						value = field.get(dto);

					} catch (IllegalArgumentException | IllegalAccessException e) {

						String message = String.format("Ocorreu um erro ao obter valor do campo \"%s\".", field.getName());
						LOG.error(message, e);

						throw new ExportExcelException(message, e);

					}

				} else if (ao instanceof Method) {

					Method method = (Method) ao;
					String methodName = method.getName();

					try {

						value = method.invoke(dto);

					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {

						String message = String.format("Ocorreu um erro ao obter valor do método \"%s\".", methodName);
						LOG.error(message, e);

						throw new ExportExcelException(message, e);

					}

				}

				return new ExportExcelColumnDTO(value, dateFormat, order, wrapText);

			}).collect(Collectors.toList());

			// Realiza ordenação apenas se "order" for utilizado em pelo menos uma prop.
			if (exportColumns.stream().anyMatch(c -> c.getOrder() > 0)) {

				exportColumns = exportColumns.stream().sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder())).collect(Collectors.toList());

			}

			lines.put(mapIndex++, exportColumns);

		}

		List<Integer> columnsToIgnore = new ArrayList<>();

		if (ignoreNullValues)
			columnsToIgnore = getColumnIndexToIgnore(lines);

		int lineIndex = 0;
		int columnIndex = 0;
		int cIndex = 0;
		SXSSFSheet sheet = workbook.createSheet(sheetName);
		Row header = sheet.createRow(lineIndex++);

		for (String columnName : columnHeaders) {

			if (columnsToIgnore.contains(columnIndex++))
				continue;

			createCell(header, cIndex++).setCellValue(columnName);

		}

		for (int key : lines.keySet()) {

			List<ExportExcelColumnDTO> columns = lines.get(key);
			Row row = sheet.createRow(lineIndex++);
			columnIndex = 0;
			cIndex = 0;

			for (ExportExcelColumnDTO column : columns) {

				if (columnsToIgnore.contains(columnIndex++))
					continue;

				Object value = column.getValue();
				String dateFormat = column.getDateFormat();
				boolean wrapText = column.isWrapText();

				Cell cell = createCell(row, cIndex++);
				setCellStyle(cell, wrapText);
				setCellValue(cell, value, dateFormat);

			}

		}

		if (autoSizeColumn) {

			sheet.trackAllColumnsForAutoSizing();

			for (int i = 0; i < columnHeaders.size(); i++) {
				sheet.autoSizeColumn(i);
			}

		}

	}

	private static List<Integer> getColumnIndexToIgnore(Map<Integer, List<ExportExcelColumnDTO>> lines) {

		List<List<Integer>> allNullValueColumns = new ArrayList<>();

		for (int key : lines.keySet()) {

			List<ExportExcelColumnDTO> values = lines.get(key);
			List<Integer> positionNulls = new ArrayList<>();

			for (int i = 0; i < values.size(); i++) {

				ExportExcelColumnDTO value = values.get(i);
				if (value == null || !value.hasValue())
					positionNulls.add(i);

			}

			allNullValueColumns.add(positionNulls);

		}

		List<Integer> columnsToIgnore = intersection(allNullValueColumns);
		return columnsToIgnore;
	}

	private static void setCellStyle(Cell cell, boolean wrapText) {

		setCellStyle(cell, wrapText, null);
	}

	private static void setCellStyle(Cell cell, boolean wrapText, Short dataFormat) {

		CellStyle cellStyle = cell.getCellStyle();
		cellStyle.setWrapText(wrapText);

		if (dataFormat != null)
			cellStyle.setDataFormat(dataFormat);
	}

	private static void setCellValue(Cell cell, Object value) throws ExportExcelException {

		setCellValue(cell, value, null);
	}

	private static void setCellValue(Cell cell, Object value, String format) throws ExportExcelException {

		Assert.notNull(cell, "Cell não pode ser null.");

		if (value == null) {
			cell.setBlank();
			return;
		}

		if (value instanceof String) {

			cell.setCellValue(value.toString());

		} else if (isDate(value)) {

			Calendar calendar = objectToCalendar(value);
			String date = StringUtils.EMPTY;

			if (StringUtils.isNotBlank(format)) {

				TimeZone timezone = calendar.getTimeZone();
				ZoneId zoneId = timezone == null ? ZoneId.systemDefault() : timezone.toZoneId();
				LocalDateTime localDateTime = LocalDateTime.ofInstant(calendar.toInstant(), zoneId);

				date = DateTimeFormatter.ofPattern(format).format(localDateTime);

			} else {
				date = DateUtils.dateOnly(calendar);
			}

			cell.setCellValue(date);

		} else if (value instanceof Boolean) {

			boolean booleanValue = value == null ? false : Boolean.valueOf(value.toString());

			cell.setCellValue(booleanValue ? "Sim" : "Não");

		} else if (value instanceof BigDecimal) {

			cell.setCellValue(((BigDecimal) value).doubleValue());

		} else if (value instanceof Double) {

			cell.setCellValue((Double) value);

		} else if (value instanceof Integer) {

			cell.setCellValue((Integer) value);

		} else if (value instanceof Collection) {

			// por hora assumi que sempre vai ser list de string
			// se existir necessidade de um tipo complexo, encaminhar para engenharia a solicitação
			@SuppressWarnings("rawtypes")
			Collection<?> listVal = (Collection) value;
			String strVal = listVal.stream().map(i -> i != null ? i.toString() : StringUtils.EMPTY).collect(Collectors.joining(NEW_LINE));
			cell.setCellValue(strVal);

		} else if (value instanceof Long) {

			cell.setCellValue((Long) value);

		} else {

			throw new ExportExcelException(String.format("O tipo %s não é suportado para exportação.", value.getClass().getTypeName()));
		}

	}

	/**
	 * Utiliza a classe para obter a anotação contendo as propriedades configuradas para gerar a planilha.
	 * 
	 * @param clazz Classe para obter a anotação {@link ExportExcelSheet}.
	 * @return Objeto contendo as configurações gerais da planilha.
	 */
	private static ExportExcelSheet getExportExcelSheetAnnotation(Class<?> clazz) {

		if (clazz.getSuperclass() != Object.class && !clazz.isAnnotationPresent(ExportExcelSheet.class))
			return getExportExcelSheetAnnotation(clazz.getSuperclass());

		ExportExcelSheet excelSheet = clazz.getAnnotation(ExportExcelSheet.class);
		return excelSheet;
	}

	private static ExportExcelSheet getSheetName(Field field) {

		if (field.isAnnotationPresent(ExportExcelSheet.class)) {
			ExportExcelSheet excelSheet = field.getAnnotation(ExportExcelSheet.class);
			return excelSheet;
		}

		return null;
	}

	private static Cell createCell(Row row, int index) {

		return row.createCell(index);
	}

	private static boolean isDate(Object value) {

		return value instanceof Date || value instanceof Calendar;
	}

	private static Calendar objectToCalendar(Object value) {

		if (!isDate(value))
			throw new ExportExcelException("O objeto não pôde ser convertido para Calendar");

		if (value instanceof Calendar) {

			return (Calendar) value;

		} else {

			Calendar c = Calendar.getInstance();
			c.setTime((Date) value);

			return c;

		}

	}

}
