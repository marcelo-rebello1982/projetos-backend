package br.com.cadastroit.services.export.excel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import br.com.cadastroit.services.exceptions.ImportFileFormatterException;
import br.com.cadastroit.services.exceptions.ImportacaoExcelException;
import br.com.cadastroit.services.utils.DateUtils;
import br.com.cadastroit.services.utils.StringUtils;
import br.com.cadastroit.services.web.dto.ImportacaoArquivoAbstractDTO;
import br.com.cadastroit.services.web.dto.ImportacaoExcelAbstractDTO;

/**
 * <p>
 * <strong>ATENÇÃO: NÃO UTILIZAR COM INJEÇÃO DE DEPENDÊNCIA</strong>
 * </p>
 * <p>
 * Classe padrão para realização de importações no sistema.<br>
 * Realiza a leitura de um arquivo XLS ou XLSX e cria os objetos que representam as linhas da planilha.
 * </p>
 * 
 * <p>
 * A leitura do arquivo será realizada utilizando os dados do cabeçalho da planilha, que deve ser definido em uma única linha e
 * estar sempre na primeira linha da planilha (índice 0).
 * </p>
 * <p>
 * Através do cabeçalho, sera possível encontrar o campo correspondente no {@link T DTO} para setar o valor quando a
 * {@link #readRow(Row) leitura da linha} for efetuada.
 * </p>
 *
 * <p>
 * Exemplo:<br>
 * Em uma planilha contendo os dados:
 * </p>
 * 
 * <pre>
 * <table border="1">
 *	<tr>
 *		<td>codigo</td>
 *		<td>nome</td>
 *	</tr>
 *	<tr>
 *		<td>ks.0092</td>
 *		<td>Xyz</td>
 *	</tr>
 * </table>
 * </pre>
 * 
 * <p>
 * Será necessário criar um DTO:
 * </p>
 *
 * <pre>
 * class AbcDTO extends {@link ImportacaoExcelAbstract} {
 * 
 *	private String codigo;
 *
 *	private String nome;
 *
 *	[getters e setters]
 * 
 * }
 * </pre>
 * 
 *
 * @param <T> DTO para criação dos objetos que representam os itens do arquivo. Deve herdar de {@link ImportacaoExcelAbstractDTO}
 *            ou {@link ImportacaoArquivoAbstractDTO}.
 */
public abstract class ImportacaoExcelAbstract<T extends ImportacaoArquivoAbstractDTO> extends ImportacaoArquivoAbstract<T> {

	private final static Logger LOG = LoggerFactory.getLogger(ImportacaoExcelAbstract.class);

	private List<ImportacaoExcelField> importacaoField = new ArrayList<>();
	private Map<String, ImportacaoExcelMetadata> mapFieldMetadata = new HashMap<>();

	protected ImportacaoExcelAbstract(Class<T> clazz) {

		super(clazz);
		this.readFieldsFromType();
	}

	/**
	 * Valida a lista de itens.<br>
	 * Aplicar aqui regras de negócio para validação dos DTOs gerados a partir do arquivo.<br>
	 * Este método é invocado antes de retornar a lista em {@link #readFile(MultipartFile)} e {@link #readFile(InputStream)}.<br>
	 * Sempre que uma validação <b>falhar</b>, lançar uma exceção reportando o erro.<br>
	 * Novos tipos de exceções podem ser criadas, porém devem herdar de {@link ImportacaoExcelException}.<br>
	 * Se não for necessário retornar erro ao usuário, utilizar {@link #filter(List)}.<br>
	 * 
	 * <pre>
	 * Exemplo: campo não permite valor nulo.
	 * </pre>
	 * 
	 * @param list Lista de itens para validação.
	 * @throws ImportacaoExcelException Caso alguma validação não esteja em conformidade.
	 */
	protected void validate(List<T> list) throws ImportacaoExcelException {

	}

	/**
	 * Se for possível filtrar itens inválidos, utilizar este método.<br>
	 * 
	 * O objetivo aqui é abstrair do usuário possíveis dados inválidos (como linha completamente vazia) e não retornar erro.<br>
	 * 
	 * Este método é invocado antes de retornar a lista em {@link #readFile(MultipartFile)} e {@link #readFile(InputStream)}.<br>
	 * Se for necessário retornar erro ao usuário, utilizar {@link #validate(List)}.
	 * 
	 * <pre>
	 * Exemplo: Ignorar itens que tenham alguma informação <code>null</code>.
	 * </pre>
	 * 
	 * @param list Lista de itens para filtrar.
	 */
	protected List<T> filter(List<T> list) {

		return list;
	}

	/**
	 * Inicia a leitura do arquivo para geração dos itens.
	 * 
	 * @param file Arquivo para leitura.
	 * @return Lista de itens.
	 * @throws ImportacaoExcelException Caso ocorra erro na leitura do arquivo ou transformação dos valores das células para DTO.
	 */
	protected List<T> readFile(MultipartFile file) throws ImportacaoExcelException {

		try (InputStream is = file.getInputStream()) {

			this.filename = file.getOriginalFilename();
			return this.readFile(is);

		} catch (IOException e) {
			String message = "Ocorreu um erro ao acessar o arquivo para importação.";
			LOG.error(message, e);
			throw new ImportacaoExcelException(message, e);
		}

	}

	/**
	 * Inicia a leitura do arquivo para geração dos itens.
	 * 
	 * @param content Arquivo para leitura no formato {@link InputStream}.
	 * @return Lista de itens.
	 * @throws ImportacaoExcelException Caso ocorra erro na leitura do arquivo ou transformação dos valores das células para DTO.
	 */
	protected List<T> readFile(InputStream content) throws ImportacaoExcelException {

		try (Workbook workbook = WorkbookFactory.create(content)) {

			if (workbook == null)
				throw new ImportacaoExcelException("Não foi possível ler o arquivo para importação.");

			Sheet sheet = this.readSheet(workbook);
			int rows = sheet.getLastRowNum();

			if (rows <= 0)
				throw new ImportacaoExcelException("O arquivo não possui linhas válidas para realizar a importação.");

			LOG.info("Iniciando leitura de arquivo: {} linhas. ", rows);

			Row rowCabecalho = sheet.getRow(0);
			this.createMapFieldCellIndex(rowCabecalho);

			List<T> list = new ArrayList<>();

			for (int i = 1; i <= rows; i++) {

				Row row = sheet.getRow(i);

				T item = this.readRow(row);

				if (item != null)
					list.add(item);
			}

			LOG.info("Finalizando leitura de arquivo: {} itens encontrados.", list.size());

			LOG.info("Validando itens");
			this.validate(list);
			LOG.info("Fim da validação dos itens");

			LOG.info("Filtrando itens: {}.", list.size());
			list = this.filter(list);
			LOG.info("Fim da filtragem dos itens: {}.", list.size());

			return list;

		} catch (ImportacaoExcelException e) {
			throw e;
		} catch (IOException e) {
			String message = "Não foi possível ler o arquivo para importação. Verifique se o mesmo está em um formato válido e aceito pelo sistema, comparando com o arquivo modelo.";
			LOG.error(message, e);
			throw new ImportacaoExcelException(message);
		}

	}

	/**
	 * Inicia a leitura do conteúdo para geração dos itens.
	 * 
	 * @param content String do conteúdo para importação.
	 * @return Lista de itens.
	 * @throws ImportacaoExcelException Caso ocorra erro na leitura do conteúdo ou transformação dos valores das colunas para DTO.
	 */
	protected List<T> readString(String content) throws ImportacaoExcelException {

		if (StringUtils.isBlank(content))
			throw new ImportacaoExcelException("Nenhum conteúdo foi informado para importação.");

		List<T> list = new ArrayList<>();
		List<String> lines = Arrays.asList(content.split(StringUtils.LF));

		if (CollectionUtils.isEmpty(lines))
			throw new ImportacaoExcelException("O conteúdo informado é inválido ou não possui linhas válidas para importação.");

		LOG.info("Iniciando leitura do conteúdo: {} linhas.", lines.size());

		String header = lines.get(0);
		this.createMapFieldCellIndex(header);

		LOG.info("Finalizando leitura do header: {} colunas mapeadas.", this.mapFieldMetadata.size());
		LOG.info("Iniciando leitura dos itens {}.", DateUtils.getCurrentTime());

		for (int i = 1; i < lines.size(); i++) {

			String line = lines.get(i);

			T dto = this.readRow(line, i);
			list.add(dto);

		}

		LOG.info("Finalizando leitura do conteúdo: {} itens encontrados", list.size());

		LOG.info("Validando itens");
		this.validate(list);
		LOG.info("Fim da validação dos itens");

		LOG.info("Filtrando itens: {}", list.size());
		list = this.filter(list);
		LOG.info("Fim da filtragem dos itens: {}", list.size());

		return list;

	}

	/**
	 * Obtém a primeira planilha do documento para leitura.
	 * 
	 * @param workbook Documento para obter planilha.
	 * @return Planilha do documento.
	 * @throws IllegalArgumentException Caso o documento seja nulo.
	 * @throws ImportacaoExcelException Caso não exista planilha no documento.
	 */
	private Sheet readSheet(Workbook workbook) throws IllegalArgumentException, ImportacaoExcelException {

		Assert.notNull(workbook, "O arquivo para obter a planilha é obrigatório.");

		Sheet sheet = workbook.getSheetAt(0);

		if (sheet == null)
			throw new ImportacaoExcelException("Não foi possível encontrar planilhas neste arquivo");

		return sheet;
	}

	/**
	 * Cria metadados das células contidas na linha de cabeçalho do arquivo para geração dos itens.<br>
	 * Cada célula do cabeçalho deve corresponder à um campo do DTO.<br>
	 * 
	 * <b>Atenção: arquivos com múltiplas linhas de cabeçalho não são suportados.</b>
	 * 
	 * @param headerRow Linha de cabeçalho da planilha, normalmente é o índice 0.
	 * @return Mapa de metadados, onde a chave é o nome do campo no DTO compatível com o nome da célula na linha de cabeçalho.
	 */
	private final void createMapFieldCellIndex(Row headerRow) {

		this.prepareToRead();
		int cells = headerRow.getLastCellNum();

		for (int i = 0; i < cells; i++) {

			Cell cell = headerRow.getCell(i);

			try {

				String cellValue = cell.getStringCellValue();

				ImportacaoExcelField importacaoField = this.getFieldsName()
						.stream()
						.filter(f -> StringUtils.isEqualIgnoreSpecial(f.getName(), StringUtils.deleteWhitespace(cellValue)) || StringUtils
								.isEqualIgnoreSpecial(StringUtils.deleteWhitespace(f.getAlias()), StringUtils.deleteWhitespace(cellValue)))
						.findFirst()
						.orElse(null);

				if (importacaoField == null) {
					LOG.warn("Nenhum campo foi encontrado utilizando o valor da célula. Índice da célula {}. Valor da célula: \"{}\"",
							cell.getColumnIndex() + 1, cellValue);

					continue;
				}

				Method setterMethod = this.getSetterMethod(importacaoField.getName());
				Class<?> returnType = this.getFieldReturnType(importacaoField.getName());

				this.mapFieldMetadata.put(importacaoField.getName(),
						new ImportacaoExcelMetadata(i, cellValue, importacaoField, setterMethod, returnType));

			} catch (NoSuchMethodException e) {

				throw new ImportacaoExcelException("Ocorreu um erro ao identificar os campos para mapeamento do arquivo.");

			} catch (Exception e) {
				LOG.error("Erro ao obter o valor da célula no formato string.", e);
			}

		}

	}

	/**
	 * Cria metadados das células contidas na linha de cabeçalho do arquivo para geração dos itens.<br>
	 * Cada célula do cabeçalho deve corresponder à um campo do DTO.<br>
	 * 
	 * <b>Atenção: arquivos com múltiplas linhas de cabeçalho não são suportados.</b>
	 * 
	 * @param header String da primeira linha do conteúdo importado.
	 */
	private void createMapFieldCellIndex(String header) {

		this.prepareToRead();
		List<String> headerItems = Arrays.asList(header.split(StringUtils.TAB));

		if (CollectionUtils.isEmpty(headerItems))
			throw new ImportacaoExcelException(
					"Não foi possível ler os cabeçalhos do conteúdo informado. Verifique se o conteúdo completo foi copiado do arquivo.");

		LOG.info("Iniciando leitura do header: {} colunas.", headerItems.size());

		for (int i = 0; i < headerItems.size(); i++) {

			try {

				String cellValue = headerItems.get(i);

				ImportacaoExcelField importacaoField = this.getFieldsName()
						.stream()
						.filter(f -> StringUtils.isEqualIgnoreSpecial(f.getName(), StringUtils.deleteWhitespace(cellValue)) || StringUtils
								.isEqualIgnoreSpecial(StringUtils.deleteWhitespace(f.getAlias()), StringUtils.deleteWhitespace(cellValue)))
						.findFirst()
						.orElse(null);

				if (importacaoField == null) {
					LOG.warn("Nenhum campo foi encontrado utilizando o valor da célula. Índice da célula {}. Valor da célula: \"{}\"", i + 1,
							cellValue);

					continue;
				}

				Method setterMethod = this.getSetterMethod(importacaoField.getName());
				Class<?> returnType = this.getFieldReturnType(importacaoField.getName());

				this.mapFieldMetadata.put(importacaoField.getName(),
						new ImportacaoExcelMetadata(i, cellValue, importacaoField, setterMethod, returnType));

			} catch (NoSuchMethodException e) {
				throw new ImportacaoExcelException("Ocorreu um erro ao identificar os campos para mapeamento do arquivo.");
			}

		}
	}

	/**
	 * Realiza a leitura de uma linha da planilha e instancia um DTO com as informações relacionadas a este item.
	 * 
	 * @param row Linha da planilha para leitura.
	 * @return Objeto que representa um item.
	 * @throws ImportacaoExcelException Se não for possível criar o DTO ou setar o valor no campo do DTO.
	 */
	private final T readRow(Row row) throws ImportacaoExcelException {

		if (row == null)
			return null;

		T dto = null;

		try {
			dto = (T) this.typeDTO.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Erro ao instanciar o DTO", e);
			throw new ImportacaoExcelException("Ocorreu um erro ao criar um item para este tipo de importação.");
		}

		int rowIndex = row.getRowNum();

		// Seta o número da linha no item
		super.setLinha(dto, rowIndex + 1);

		for (String field : this.getMapFieldMetadata().keySet()) {

			ImportacaoExcelMetadata metadata = this.getMapFieldMetadata().get(field);
			Cell cell = row.getCell(metadata.getPosicao());

			if (cell == null) {
				LOG.info("Linha {} - Célula inexistente ou sem conteúdo na coluna {}.", rowIndex + 1, metadata.getPosicao());
				continue;
			}

			Method method = metadata.getSetterMethod();
			ImportExcelFormatter<?> formatter = null;

			try {

				Class<? extends ImportExcelFormatter<?>> f = metadata.getImportacaoField().getFormatter();

				if (f != null)
					formatter = (ImportExcelFormatter<?>) f.newInstance();

			} catch (InstantiationException | IllegalAccessException e) {

				LOG.error("Não foi possível instanciar o formatter \"{}\".", metadata.getImportacaoField().getFormatter().getCanonicalName(), e);

			}

			try {

				switch (cell.getCellType()) {
					case STRING:

						String value = cell.getStringCellValue();

						if (StringUtils.isNotBlank(value)) {
							value = value.trim();

							if (formatter != null) {

								Object valueFormated = formatter.format(value);
								method.invoke(dto, valueFormated);

							} else {

								method.invoke(dto, value);

							}

						}

						break;

					case BOOLEAN:
						boolean bol = cell.getBooleanCellValue();
						method.invoke(dto, bol);
						break;

					case NUMERIC:

						Double num = cell.getNumericCellValue();
						Class<?> returnType = metadata.getReturnType();

						if (cell.getCellStyle() != null && StringUtils.isNotBlank(cell.getCellStyle().getDataFormatString())
								&& cell.getCellStyle().getDataFormatString().contains(StringUtils.SYMBOL_PERCENT)) {
							num = num * 100;
						}

						Object val = this.numericToReturnType(returnType, num);
						method.invoke(dto, val);

						break;

					case _NONE:
					case BLANK:
					case ERROR:
					case FORMULA:
					default:
						LOG.warn("O tipo da célula \"{}\" não é suportado como valor para campos do DTO.", cell.getCellType().name());
						break;
				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ImportFileFormatterException e) {
				LOG.error("Linha {} - Ocorreu um erro ao ler o valor da célula \"{}\" para setar em \"{}\" do DTO.", rowIndex + 1,
						metadata.getCellValue(), method.getName(), e);

				String errorMessage = String.format("Não foi possível ler o valor da coluna \"%s\" na linha %d.", metadata.getCellValue(),
						rowIndex + 1);

				if (e instanceof ImportFileFormatterException)
					errorMessage = errorMessage.concat(StringUtils.SPACE + e.getMessage());

				if (this.stopReadOnError())
					throw new ImportacaoExcelException(errorMessage);
				else {
					errors.add(errorMessage);
					dto = null;
					break;
				}
			}

		}

		return dto;

	}

	private T readRow(String line, int lineIndex) {

		T dto = null;

		try {
			dto = (T) this.typeDTO.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOG.error("Erro ao instanciar o DTO", e);
			throw new ImportacaoExcelException("Ocorreu um erro ao criar um item para este tipo de importação.");
		}

		// Seta o número da linha no item
		super.setLinha(dto, lineIndex + 1);

		List<String> columns = Arrays.asList(line.split(StringUtils.TAB));

		for (String field : this.getMapFieldMetadata().keySet()) {

			ImportacaoExcelMetadata metadata = this.getMapFieldMetadata().get(field);

			if (metadata.getPosicao() >= columns.size()) {
				LOG.info("Linha {} - Célula inexistente ou sem conteúdo na coluna {}.", lineIndex + 1, metadata.getPosicao());
				continue;
			}

			String value = columns.get(metadata.getPosicao());

			if (StringUtils.isBlank(value)) {
				LOG.info("Linha {} - Célula inexistente ou sem conteúdo na coluna {}.", lineIndex + 1, metadata.getPosicao());
				continue;
			}

			Method method = metadata.getSetterMethod();
			ImportExcelFormatter<?> formatter = null;

			try {

				Class<? extends ImportExcelFormatter<?>> f = metadata.getImportacaoField().getFormatter();

				if (f != null)
					formatter = (ImportExcelFormatter<?>) f.newInstance();

			} catch (InstantiationException | IllegalAccessException e) {

				LOG.error("Não foi possível instanciar o formatter \"{}\".", metadata.getImportacaoField().getFormatter().getCanonicalName(), e);

			}

			try {

				if (formatter != null) {

					Object valueFormated = formatter.format(value);
					method.invoke(dto, valueFormated);

				} else {

					Class<?> returnType = metadata.getReturnType();

					Object val = this.stringToReturnType(returnType, value);
					method.invoke(dto, val);

				}

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | ImportFileFormatterException e) {
				LOG.error("Linha {} - Ocorreu um erro ao ler o valor da célula para setar em \"{}\" do DTO.", lineIndex + 1, method.getName(), e);

				String errorMessage = String.format("Não foi possível ler o valor da célula %d na linha %d.", metadata.getPosicao() + 1,
						lineIndex + 1);

				if (e instanceof ImportFileFormatterException)
					errorMessage = errorMessage.concat(StringUtils.SPACE + e.getMessage());

				if (this.stopReadOnError())
					throw new ImportacaoExcelException(errorMessage);
				else {
					errors.add(errorMessage);
					dto = null;
					break;
				}

			}
		}

		return dto;

	}

	/**
	 * Obtém os fields do dto para alimentar a lista de {@link ImportacaoExcelField} contendo dados e configurações de cada campo.
	 */
	private void readFieldsFromType() {

		if (CollectionUtils.isEmpty(this.declaredFields))
			throw new ImportacaoExcelException("Não foi possível obter os campos do dto.");

		for (Field field : this.declaredFields) {

			String fieldName = field.getName();
			String alias = StringUtils.EMPTY;
			Class<? extends ImportExcelFormatter<?>> formatter = null;

			ImportExcelColumn importExcelColumn = this.getImportExcelColumn(fieldName);

			if (importExcelColumn != null) {

				if (StringUtils.isNotBlank(importExcelColumn.name()))
					alias = importExcelColumn.name();

				if (importExcelColumn.formatter() != null
						&& !importExcelColumn.formatter().getCanonicalName().equals(ImportExcelColumn.DEFAULT.class.getCanonicalName()))
					formatter = importExcelColumn.formatter();

			}

			ImportacaoExcelField importacaoField = new ImportacaoExcelField(fieldName, alias, formatter);

			this.importacaoField.add(importacaoField);

		}
	}

	/**
	 * Converte o valor da célula, fornecido em {@link Double}, para o tipo desejado através do parâmetro {@code returnType}.
	 * 
	 * @param returnType Tipo para realizar a conversão de {@code value}.
	 * @param value Valor para converter.
	 * @return Valor convertido, no tipo {@code returnType}.
	 */
	private <R> R numericToReturnType(Class<R> returnType, Double value) {

		if (returnType == int.class || returnType == Integer.class) {
			return returnType.cast(value.intValue());
		} else if (returnType == long.class || returnType == Long.class) {
			return returnType.cast(value.longValue());
		} else if (returnType == float.class || returnType == Float.class) {
			return returnType.cast(value.floatValue());
		} else if (returnType == double.class || returnType == Double.class) {
			return returnType.cast(value);
		} else if (returnType == BigDecimal.class) {
			return returnType.cast(new BigDecimal(value));
		} else if (returnType == Date.class) {
			Date date = HSSFDateUtil.getJavaDate(value);
			return returnType.cast(date);
		} else if (returnType == Calendar.class) {
			Calendar calendar = HSSFDateUtil.getJavaCalendar(value);
			return returnType.cast(calendar);
		} else if (returnType == String.class) {
			return returnType.cast(new BigDecimal(value).toString());
		}

		throw new ImportacaoExcelException(String.format("O tipo \"%s\" não é suportado.", returnType.toString()));
	}

	private List<ImportacaoExcelField> getFieldsName() {

		return importacaoField;
	}

	private Map<String, ImportacaoExcelMetadata> getMapFieldMetadata() {

		if (mapFieldMetadata == null)
			mapFieldMetadata = new HashMap<>();

		return mapFieldMetadata;
	}

	private ImportExcelColumn getImportExcelColumn(String fieldName) {

		ImportExcelColumn annotation = null;

		try {

			Field field = this.declaredFields.stream().filter(f -> StringUtils.isEqualIgnoreSpecial(f.getName(), fieldName)).findFirst().orElse(null);

			if (field != null)
				annotation = field.getAnnotation(ImportExcelColumn.class);

		} catch (Exception e) {
			LOG.error("Erro ao obter a anotação ImportExcelColumn do campo \"{}\"", fieldName, e);
		}

		return annotation;

	}

	private void clearFieldMetadata() {

		this.mapFieldMetadata = new HashMap<>();
	}

	private void clearErrorsList() {

		this.errors = new ArrayList<>();
	}

	private void prepareToRead() {

		this.clearFieldMetadata();
		this.clearErrorsList();
	}

	/**
	 * Classe de metadados para posição do campo na planilha, método set no {@link T DTO} e tipo do retorno do campo.<br>
	 * <strong>Não existe a necessidade de ser pública porque é utilizada internamente nesta classe e pode ser utilizada apenas em
	 * classes derivadas.</strong>
	 */
	protected class ImportacaoExcelMetadata {

		private int posicao;
		private String cellValue;
		private ImportacaoExcelField importacaoField;
		private Method setterMethod;
		private Class<?> returnType;

		public ImportacaoExcelMetadata(int posicao, String cellValue, ImportacaoExcelField importacaoField, Method setterMethod,
				Class<?> returnType) {

			this.posicao = posicao;
			this.cellValue = cellValue;
			this.importacaoField = importacaoField;
			this.setterMethod = setterMethod;
			this.returnType = returnType;
		}

		public int getPosicao() {

			return posicao;
		}

		public String getCellValue() {

			return cellValue;
		}

		public ImportacaoExcelField getImportacaoField() {

			return importacaoField;
		}

		public Method getSetterMethod() {

			return setterMethod;
		}

		public Class<?> getReturnType() {

			return returnType;
		}

	}

	protected class ImportacaoExcelField {

		private String name;
		private String alias;
		private Class<? extends ImportExcelFormatter<?>> formatter;

		public ImportacaoExcelField(String name, String alias, Class<? extends ImportExcelFormatter<?>> formatter) {

			this.name = name;
			this.alias = alias;
			this.formatter = formatter;
		}

		public String getName() {

			return name;
		}

		public String getAlias() {

			return alias;
		}

		public Class<? extends ImportExcelFormatter<?>> getFormatter() {

			return formatter;
		}
	}

}
