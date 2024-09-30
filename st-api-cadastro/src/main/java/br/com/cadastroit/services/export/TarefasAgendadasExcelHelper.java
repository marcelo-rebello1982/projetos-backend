package br.com.cadastroit.services.export;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import br.com.cadastroit.services.exceptions.BusinessException;
import br.com.cadastroit.services.export.excel.ImportacaoExcelAbstract;
import br.com.cadastroit.services.utils.DateUtils;
import br.com.cadastroit.services.utils.DocumentoUtils;
import br.com.cadastroit.services.web.dto.BalancoEstoqueImportacaoArquivoItemErroDTO;
import br.com.cadastroit.services.web.dto.BalancoEstoquePesquisaDTO;
import br.com.cadastroit.services.web.dto.BalancoEstoqueProdutoDTO;
import br.com.cadastroit.services.web.dto.BalancoEstoqueProdutoZeramentoDTO;
import br.com.cadastroit.services.web.dto.BalancoEstoqueTotalizadorDTO;
import br.com.cadastroit.services.web.dto.InventarioConsultaResultadoDTO;
import br.com.cadastroit.services.web.dto.TarefaDTO;
import lombok.Builder;

@Builder
public class TarefasAgendadasExcelHelper extends ImportacaoExcelAbstract<BalancoEstoqueImportacaoItemDTO> {

	private final static int ROWS_IN_MEMORY = 1000;

	public TarefasAgendadasExcelHelper() {

		super(BalancoEstoqueImportacaoItemDTO.class);
	}

	public List<BalancoEstoqueImportacaoItemDTO> lerArquivo(MultipartFile file) {

		return super.readFile(file);
	}

	public byte[] gerarArquivoProdutos(List<BalancoEstoqueImportacaoArquivoItemErroDTO> produtosInvalidos, List<BalancoEstoqueImportacaoArquivoItemErroDTO> produtosNaoEncontrados) {

		SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);

		CreationHelper createHelper = wb.getCreationHelper();
		CellStyle cellStyleDouble = wb.createCellStyle();

		SXSSFSheet produtosInvalidosSheet = null;
		SXSSFSheet produtosNaoEncontradosSheet = null;
		String naoQualificados = "Produtos inválidos";
		String naoEncontrados = "Produtos não encontrados";

		if (!produtosInvalidos.isEmpty() && !produtosNaoEncontrados.isEmpty()) {
			produtosInvalidosSheet = wb.createSheet(naoQualificados);
			produtosNaoEncontradosSheet = wb.createSheet(naoEncontrados);
		} else if (!produtosInvalidos.isEmpty()) {
			produtosInvalidosSheet = wb.createSheet(naoQualificados);
		} else {
			produtosNaoEncontradosSheet = wb.createSheet(naoEncontrados);
		}

		if (produtosInvalidosSheet != null) {
			produtosInvalidosSheet.trackAllColumnsForAutoSizing();

			int rowCount = 0;

			Row rowHeader = produtosInvalidosSheet.createRow(rowCount++);

			int colCount = 0;
			rowHeader.createCell(colCount++).setCellValue("Código");
			rowHeader.createCell(colCount++).setCellValue("Descrição");
			rowHeader.createCell(colCount++).setCellValue("Unidade");
			rowHeader.createCell(colCount++).setCellValue("Quantidade");
			rowHeader.createCell(colCount++).setCellValue("Erro");

			for (BalancoEstoqueImportacaoArquivoItemErroDTO produto : produtosInvalidos) {

				colCount = 0;

				Row rowLine = produtosInvalidosSheet.createRow(rowCount++);

				rowLine.createCell(colCount++).setCellValue(produto.getCodigo());
				rowLine.createCell(colCount++).setCellValue(produto.getNome());
				rowLine.createCell(colCount++).setCellValue(produto.getUnidadeMedida());

				String format = "0";

				if (produto.getCasasDecimaisQuantidade() > 0)
					format += "." + StringUtils.repeat("0", produto.getCasasDecimaisQuantidade());

				short formatDouble = createHelper.createDataFormat().getFormat(format);
				cellStyleDouble.setDataFormat(formatDouble);

				Cell cellQuantidade = rowLine.createCell(colCount++);
				cellQuantidade.setCellStyle(cellStyleDouble);
				cellQuantidade.setCellValue(produto.getQuantidade().doubleValue());

				rowLine.createCell(colCount++).setCellValue(produto.getMensagem());
			}

			for (int i = 0; i < colCount; i++)
				produtosInvalidosSheet.autoSizeColumn(i);

		}

		if (produtosNaoEncontradosSheet != null) {
			produtosNaoEncontradosSheet.trackAllColumnsForAutoSizing();

			int rowCount = 0;

			Row rowHeader = produtosNaoEncontradosSheet.createRow(rowCount++);

			int colCount = 0;
			rowHeader.createCell(colCount++).setCellValue("Código");
			rowHeader.createCell(colCount++).setCellValue("Quantidade");

			short format = createHelper.createDataFormat().getFormat("0.0000");
			cellStyleDouble.setDataFormat(format);

			for (BalancoEstoqueImportacaoArquivoItemErroDTO produto : produtosNaoEncontrados) {

				colCount = 0;

				Row rowLine = produtosNaoEncontradosSheet.createRow(rowCount++);

				rowLine.createCell(colCount++).setCellValue(produto.getCodigo());

				Cell cellQuantidade = rowLine.createCell(colCount++);
				cellQuantidade.setCellStyle(cellStyleDouble);
				cellQuantidade.setCellValue(produto.getQuantidade().doubleValue());

			}

			for (int i = 0; i < colCount; i++)
				produtosNaoEncontradosSheet.autoSizeColumn(i);
		}

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		try {
			wb.write(bOut);
			wb.close();
		} catch (IOException e) {
			throw new BusinessException("Ocorreu um erro ao gerar o arquivo de produtos.");
		}

		return bOut.toByteArray();
	}

	public byte[] exportarXls(List<TarefaDTO> tarefaDto, List<BalancoEstoquePesquisaDTO> pesquisaDTO, boolean mostrarTotalDeRegistros) {

		SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);

		SXSSFSheet balancosSheet = wb.createSheet("Tarefas");

		CreationHelper createHelper = wb.getCreationHelper();

		CellStyle cellStyleDouble = wb.createCellStyle();
		short formatDouble = createHelper.createDataFormat().getFormat("0.00");
		cellStyleDouble.setDataFormat(formatDouble);

		CellStyle cellStyleEntidade = wb.createCellStyle();
		cellStyleEntidade.setWrapText(true);

		balancosSheet.trackAllColumnsForAutoSizing();

		int rowCount = 0;

		Row rowHeader = balancosSheet.createRow(rowCount++);

		int colCount = 0;
		rowHeader.createCell(colCount++).setCellValue("Titulo tarefa");
		rowHeader.createCell(colCount++).setCellValue("Descricao da tarefa");
		rowHeader.createCell(colCount++).setCellValue("tempo gasto");
		rowHeader.createCell(colCount++).setCellValue("prazo de término");
		rowHeader.createCell(colCount++).setCellValue("data de inicio");
		rowHeader.createCell(colCount++).setCellValue("data de término");
		rowHeader.createCell(colCount++).setCellValue("hora de início");
		rowHeader.createCell(colCount++).setCellValue("hora de término");
		rowHeader.createCell(colCount++).setCellValue("encerrado");
		rowHeader.createCell(colCount++).setCellValue("departamento");
		rowHeader.createCell(colCount++).setCellValue("data de atualização");
		rowHeader.createCell(colCount++).setCellValue("pessoa");
		

		if (mostrarTotalDeRegistros)
			rowHeader.createCell(colCount++).setCellValue("Custo total");

		for (TarefaDTO tarefa : tarefaDto) {

			colCount = 0;
			Row rowLine = balancosSheet.createRow(rowCount++);
			
			rowLine.createCell(colCount++).setCellValue(tarefa.getTitulo());
			
			rowLine.createCell(colCount++).setCellValue(tarefa.getDescr());
			
			rowLine.createCell(colCount++).setCellValue(tarefa.getPrazo());

			rowLine.createCell(colCount++).setCellValue(DateUtils.fullDate(DateUtils.dateToCalendar(tarefa.getDataInicio())));

			rowLine.createCell(colCount++).setCellValue(DateUtils.dateOnly(DateUtils.dateToCalendar(tarefa.getDataFinal())));

		

			Cell cellEntidade = rowLine.createCell(colCount++);
			cellEntidade.setCellStyle(cellStyleEntidade);
			cellEntidade.setCellValue(tarefa.getTempoGasto() + "\n" + DocumentoUtils.formatar(tarefa.getTitulo()));


			rowLine.createCell(colCount++).setCellValue(tarefa.getDataInicio());

			rowLine.createCell(colCount++).setCellValue(tarefa.getDataFinal());

			if (mostrarTotalDeRegistros) {
				Cell cellCustoTotal = rowLine.createCell(colCount++);
				cellCustoTotal.setCellStyle(cellStyleDouble);
				cellCustoTotal.setCellValue(tarefa.getPessoaId().doubleValue());
			}
		}

		for (int i = 0; i < colCount; i++)
			balancosSheet.autoSizeColumn(i);

		try {
			return retornarXls(wb);
		} catch (IOException e) {
			throw new BusinessException("Ocorreu um erro ao gerar o arquivo de balanço.");
		}

	}

	public byte[] exportarAnalitico(List<BalancoEstoqueProdutoZeramentoDTO> itensZeramento, int casasDecimaisPrecoCusto) {

		SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);

		SXSSFSheet itensZeramentoSheet = wb.createSheet("Itens de zeramento");

		CreationHelper createHelper = wb.getCreationHelper();

		CellStyle cellStyleCusto = wb.createCellStyle();
		String formatCustoString = "0." + StringUtils.repeat("0", casasDecimaisPrecoCusto);
		short formatCusto = createHelper.createDataFormat().getFormat(formatCustoString);
		cellStyleCusto.setDataFormat(formatCusto);

		CellStyle cellStyleQuantide = wb.createCellStyle();

		itensZeramentoSheet.trackAllColumnsForAutoSizing();

		int rowCount = 0;

		Row rowHeader = itensZeramentoSheet.createRow(rowCount++);

		int colCount = 0;
		rowHeader.createCell(colCount++).setCellValue("Código");
		rowHeader.createCell(colCount++).setCellValue("Produto");
		rowHeader.createCell(colCount++).setCellValue("Unidade");
		rowHeader.createCell(colCount++).setCellValue("Custo");
		rowHeader.createCell(colCount++).setCellValue("Quantidade original");
		rowHeader.createCell(colCount++).setCellValue("Quantidade informada");
		rowHeader.createCell(colCount++).setCellValue("Diferença");

		for (BalancoEstoqueProdutoZeramentoDTO item : itensZeramento) {

			colCount = 0;
			Row rowLine = itensZeramentoSheet.createRow(rowCount++);

			rowLine.createCell(colCount++).setCellValue(item.getCodigo());
			rowLine.createCell(colCount++).setCellValue(item.getNome());
			rowLine.createCell(colCount++).setCellValue(item.getSiglaUnidadeMedida());

			Cell cellCusto = rowLine.createCell(colCount++);
			cellCusto.setCellStyle(cellStyleCusto);
			cellCusto.setCellValue(item.getValorCustoMedio().doubleValue());

			String format = "0.0000";
			int casasDecimais = item.getCasasDecimaisQuantidade();

			if (casasDecimais > 4)
				format += StringUtils.repeat("0", casasDecimais - 4);

			short dataFormatQuantidade = createHelper.createDataFormat().getFormat(format);
			cellStyleQuantide.setDataFormat(dataFormatQuantidade);

			BigDecimal quantidadeEstoque = item.getQuantidadeEstoque();

			Cell cellQuantidadeOriginal = rowLine.createCell(colCount++);
			cellQuantidadeOriginal.setCellStyle(cellStyleQuantide);
			cellQuantidadeOriginal.setCellValue(quantidadeEstoque.doubleValue());

			Cell cellQuantidadeInformada = rowLine.createCell(colCount++);
			cellQuantidadeInformada.setCellStyle(cellStyleQuantide);
			cellQuantidadeInformada.setCellValue(0);

			Cell cellDiferenca = rowLine.createCell(colCount++);
			cellDiferenca.setCellStyle(cellStyleQuantide);
			cellDiferenca.setCellValue(quantidadeEstoque.negate().doubleValue());
		}

		for (int i = 0; i < colCount; i++)
			itensZeramentoSheet.autoSizeColumn(i);


		try {
			return retornarXls(wb);
		} catch (IOException e) {
			throw new BusinessException("Ocorreu um erro ao gerar o arquivo de balanço.");
		}

	}
	
	private byte[] retornarXls(SXSSFWorkbook wb) throws IOException {

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		wb.write(bOut);
		wb.close();

		return bOut.toByteArray();
	}

	public byte[] exportarDetalhado(Map<Long, InventarioConsultaResultadoDTO> produtoConsultaInventario, List<BalancoEstoqueProdutoDTO> produtos, BalancoEstoqueTotalizadorDTO totalizadorZeramento, int casasDecimaisPrecoCusto)
			throws Exception {

		boolean isZeramentoEstoque = totalizadorZeramento != null;

		SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);

		SXSSFSheet sheetBalanco = wb.createSheet("Itens de balanço");
		sheetBalanco.trackAllColumnsForAutoSizing();

		SXSSFSheet sheetZeramento = null;

		if (isZeramentoEstoque) {
			sheetZeramento = wb.createSheet("Itens de zeramento");
			sheetZeramento.trackAllColumnsForAutoSizing();
		}

		String erro = "Erro";

		CreationHelper createHelper = wb.getCreationHelper();

		String formatPrecoCusto = "0." + StringUtils.repeat("0", casasDecimaisPrecoCusto);
		short dataFormatPrecoCusto = createHelper.createDataFormat().getFormat(formatPrecoCusto);
		CellStyle cellStyleValor = wb.createCellStyle();
		cellStyleValor.setDataFormat(dataFormatPrecoCusto);

		// planilha de balanço

		int rowCount = 0;

		Row rowHeader = sheetBalanco.createRow(rowCount++);

		int colCount = 0;
		rowHeader.createCell(colCount++).setCellValue("Código do produto");
		rowHeader.createCell(colCount++).setCellValue("Nome do produto");
		rowHeader.createCell(colCount++).setCellValue("Unidade");
		rowHeader.createCell(colCount++).setCellValue("Quantidade original");
		rowHeader.createCell(colCount++).setCellValue("Valor de custo médio");
		rowHeader.createCell(colCount++).setCellValue("Preço de venda");
		rowHeader.createCell(colCount++).setCellValue("Valor de custo médio total");
		rowHeader.createCell(colCount++).setCellValue("Valor total de venda");
		rowHeader.createCell(colCount++).setCellValue("Quantidade contada");
		rowHeader.createCell(colCount++).setCellValue("Valor de custo total");
		rowHeader.createCell(colCount++).setCellValue("Quantidade ajustada");

		System.out.println("Início planilha produtos - " + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(System.currentTimeMillis())));

		int casasDecimaisDefault = 4;
		String formatQuantidadeString = "0.0000";

		for (BalancoEstoqueProdutoDTO produto : produtos) {

			colCount = 0;
			Row rowLine = sheetBalanco.createRow(rowCount++);

			InventarioConsultaResultadoDTO consultaInventario = produtoConsultaInventario.get(produto.getProduto());
			BigDecimal custoMedio = consultaInventario != null ? consultaInventario.getCustoReferencialIfCustoMedioZero() : BigDecimal.ZERO;
			BigDecimal quantidadeContada = produto.getQuantidade();
			BigDecimal quantidadeEstoque = produto.getQuantidadeEstoque();

			int produtoCasasDecimais = produto.getCasasDecimaisQuantidade();
			String format = null;
			if (produtoCasasDecimais > casasDecimaisDefault)
				format = formatQuantidadeString + StringUtils.repeat("0", produtoCasasDecimais - casasDecimaisDefault);
			else
				format = formatQuantidadeString;

			short formatQuantidade = createHelper.createDataFormat().getFormat(format);
			CellStyle cellStyleQuantidade = wb.createCellStyle();
			cellStyleQuantidade.setDataFormat(formatQuantidade);

			rowLine.createCell(colCount++).setCellValue(produto.getCodigo());
			rowLine.createCell(colCount++).setCellValue(produto.getNome());
			rowLine.createCell(colCount++).setCellValue(produto.getSiglaUnidadeMedida());

			if (consultaInventario == null) {
				rowLine.createCell(colCount++).setCellValue(erro);
				rowLine.createCell(colCount++).setCellValue(erro);
				rowLine.createCell(colCount++).setCellValue(erro);
				rowLine.createCell(colCount++).setCellValue(erro);
				rowLine.createCell(colCount++).setCellValue(erro);

				Cell cellQuantidadeContada = rowLine.createCell(colCount++);
				cellQuantidadeContada.setCellStyle(cellStyleQuantidade);
				cellQuantidadeContada.setCellValue(quantidadeContada.doubleValue());

				rowLine.createCell(colCount++).setCellValue(erro);
				rowLine.createCell(colCount++).setCellValue(erro);
			} else {

				Cell cellQuantidade = rowLine.createCell(colCount++);
				cellQuantidade.setCellStyle(cellStyleQuantidade);
				cellQuantidade.setCellValue(quantidadeEstoque.doubleValue());

				Cell cellCustoMedio = rowLine.createCell(colCount++);
				cellCustoMedio.setCellStyle(cellStyleValor);
				cellCustoMedio.setCellValue(custoMedio.doubleValue());

				Cell cellPrecoVenda = rowLine.createCell(colCount++);
				cellPrecoVenda.setCellStyle(cellStyleValor);
				cellPrecoVenda.setCellValue(consultaInventario.getPrecoVenda().doubleValue());

				Cell cellCustoMedioTotal = rowLine.createCell(colCount++);
				cellCustoMedioTotal.setCellStyle(cellStyleValor);
				cellCustoMedioTotal.setCellValue((custoMedio.multiply(quantidadeEstoque)).doubleValue());

				Cell cellValorTotalVenda = rowLine.createCell(colCount++);
				cellValorTotalVenda.setCellStyle(cellStyleValor);
				cellValorTotalVenda.setCellValue((consultaInventario.getPrecoVenda().multiply(quantidadeEstoque)).doubleValue());

				Cell cellQuantidadeContada = rowLine.createCell(colCount++);
				cellQuantidadeContada.setCellStyle(cellStyleQuantidade);
				cellQuantidadeContada.setCellValue(quantidadeContada.doubleValue());

				Cell cellCustoTotal = rowLine.createCell(colCount++);
				cellCustoTotal.setCellStyle(cellStyleValor);
				cellCustoTotal.setCellValue((custoMedio.multiply(quantidadeContada)).doubleValue());

				Cell cellQuantidadeAjustada = rowLine.createCell(colCount++);
				cellQuantidadeAjustada.setCellStyle(cellStyleQuantidade);
				cellQuantidadeAjustada.setCellValue((quantidadeContada.subtract(quantidadeEstoque)).doubleValue());
			}
		}

		for (int i = 0; i < colCount; i++)
			sheetBalanco.autoSizeColumn(i);

		System.out.println("Fim planilha produtos - " + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(System.currentTimeMillis())));

		// Planilha de resumo de itens de zeramento

		if (isZeramentoEstoque) {
			rowCount = 0;

			rowHeader = sheetZeramento.createRow(rowCount++);

			colCount = 0;
			rowHeader.createCell(colCount++).setCellValue("Operação");
			rowHeader.createCell(colCount++).setCellValue("Quantidade de itens");
			rowHeader.createCell(colCount++).setCellValue("Valor total");

			CellStyle cellStylePreco = wb.createCellStyle();
			short custoFormat = createHelper.createDataFormat().getFormat("0.00");
			cellStylePreco.setDataFormat(custoFormat);

			System.out.println("Início planilha zeramento - " + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(System.currentTimeMillis())));

			colCount = 0;
			Row rowLine = sheetZeramento.createRow(rowCount++);

			rowLine.createCell(colCount++).setCellValue("Entrada");
			rowLine.createCell(colCount++).setCellValue(totalizadorZeramento.getItensEntrada());

			Cell cellCustoTotal = rowLine.createCell(colCount++);
			cellCustoTotal.setCellStyle(cellStylePreco);
			cellCustoTotal.setCellValue(totalizadorZeramento.getCustoItensEntrada().doubleValue());

			colCount = 0;
			rowLine = sheetZeramento.createRow(rowCount++);

			rowLine.createCell(colCount++).setCellValue("Saída");
			rowLine.createCell(colCount++).setCellValue(totalizadorZeramento.getItensSaida());

			cellCustoTotal = rowLine.createCell(colCount++);
			cellCustoTotal.setCellStyle(cellStylePreco);
			cellCustoTotal.setCellValue(totalizadorZeramento.getCustoItensSaida().doubleValue());

			for (int i = 0; i < colCount; i++)
				sheetZeramento.autoSizeColumn(i);
		}

		System.out.println("Fim criação planilhas - " + new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(System.currentTimeMillis())));

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		try {
			wb.write(bOut);
			wb.close();
		} catch (IOException e) {
			throw new BusinessException("Ocorreu um erro ao gerar o arquivo de detalhamento do balanço.");
		}

		return bOut.toByteArray();
	}

	@Override
	protected List<BalancoEstoqueImportacaoItemDTO> filter(List<BalancoEstoqueImportacaoItemDTO> itens) {

		return itens.stream().filter(item -> item.getCodigo() != null).collect(Collectors.toList());
	}
	
	public byte[] exportarPdf(List<TarefaDTO> tarefaDto, List<BalancoEstoquePesquisaDTO> pesquisaDTO, boolean mostrarTotalDeRegistros) {

		SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		try {
			wb.write(bOut);
			wb.close();
		} catch (IOException e) {
			throw new BusinessException("Ocorreu um erro ao gerar o arquivo de detalhamento do balanço.");
		}

		return bOut.toByteArray();

	}
	
}
