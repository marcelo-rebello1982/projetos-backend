package br.com.cadastroit.services.api.util;


public class ExcelHelper {
	
	
//	public byte[] gerarArquivoProdutos(List<BalancoEstoqueImportacaoArquivoItemErroDTO> produtosInvalidos, List<BalancoEstoqueImportacaoArquivoItemErroDTO> produtosNaoEncontrados) {
//
//		SXSSFWorkbook wb = new SXSSFWorkbook(ROWS_IN_MEMORY);
//
//		CreationHelper createHelper = wb.getCreationHelper();
//		CellStyle cellStyleDouble = wb.createCellStyle();
//
//		SXSSFSheet produtosInvalidosSheet = null;
//		SXSSFSheet produtosNaoEncontradosSheet = null;
//		String naoQualificados = "Produtos inválidos";
//		String naoEncontrados = "Produtos não encontrados";
//
//		if (!produtosInvalidos.isEmpty() && !produtosNaoEncontrados.isEmpty()) {
//			produtosInvalidosSheet = wb.createSheet(naoQualificados);
//			produtosNaoEncontradosSheet = wb.createSheet(naoEncontrados);
//		} else if (!produtosInvalidos.isEmpty()) {
//			produtosInvalidosSheet = wb.createSheet(naoQualificados);
//		} else {
//			produtosNaoEncontradosSheet = wb.createSheet(naoEncontrados);
//		}
//
//		if (produtosInvalidosSheet != null) {
//			produtosInvalidosSheet.trackAllColumnsForAutoSizing();
//
//			int rowCount = 0;
//
//			Row rowHeader = produtosInvalidosSheet.createRow(rowCount++);
//
//			int colCount = 0;
//			rowHeader.createCell(colCount++).setCellValue("Código");
//			rowHeader.createCell(colCount++).setCellValue("Descrição");
//			rowHeader.createCell(colCount++).setCellValue("Unidade");
//			rowHeader.createCell(colCount++).setCellValue("Quantidade");
//			rowHeader.createCell(colCount++).setCellValue("Erro");
//
//			for (BalancoEstoqueImportacaoArquivoItemErroDTO produto : produtosInvalidos) {
//
//				colCount = 0;
//
//				Row rowLine = produtosInvalidosSheet.createRow(rowCount++);
//
//				rowLine.createCell(colCount++).setCellValue(produto.getCodigo());
//				rowLine.createCell(colCount++).setCellValue(produto.getNome());
//				rowLine.createCell(colCount++).setCellValue(produto.getUnidadeMedida());
//
//				String format = "0";
//
//				if (produto.getCasasDecimaisQuantidade() > 0)
//					format += "." + StringUtils.repeat("0", produto.getCasasDecimaisQuantidade());
//
//				short formatDouble = createHelper.createDataFormat().getFormat(format);
//				cellStyleDouble.setDataFormat(formatDouble);
//
//				Cell cellQuantidade = rowLine.createCell(colCount++);
//				cellQuantidade.setCellStyle(cellStyleDouble);
//				cellQuantidade.setCellValue(produto.getQuantidade().doubleValue());
//
//				rowLine.createCell(colCount++).setCellValue(produto.getMensagem());
//			}
//
//			for (int i = 0; i < colCount; i++)
//				produtosInvalidosSheet.autoSizeColumn(i);
//
//		}
//
//		if (produtosNaoEncontradosSheet != null) {
//			produtosNaoEncontradosSheet.trackAllColumnsForAutoSizing();
//
//			int rowCount = 0;
//
//			Row rowHeader = produtosNaoEncontradosSheet.createRow(rowCount++);
//
//			int colCount = 0;
//			rowHeader.createCell(colCount++).setCellValue("Código");
//			rowHeader.createCell(colCount++).setCellValue("Quantidade");
//
//			short format = createHelper.createDataFormat().getFormat("0.0000");
//			cellStyleDouble.setDataFormat(format);
//
//			for (BalancoEstoqueImportacaoArquivoItemErroDTO produto : produtosNaoEncontrados) {
//
//				colCount = 0;
//
//				Row rowLine = produtosNaoEncontradosSheet.createRow(rowCount++);
//
//				rowLine.createCell(colCount++).setCellValue(produto.getCodigo());
//
//				Cell cellQuantidade = rowLine.createCell(colCount++);
//				cellQuantidade.setCellStyle(cellStyleDouble);
//				cellQuantidade.setCellValue(produto.getQuantidade().doubleValue());
//
//			}
//
//			for (int i = 0; i < colCount; i++)
//				produtosNaoEncontradosSheet.autoSizeColumn(i);
//		}
//
//		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
//
//		try {
//			wb.write(bOut);
//			wb.close();
//		} catch (IOException e) {
//			throw new BusinessException("Ocorreu um erro ao gerar o arquivo de produtos.");
//		}
//
//		return bOut.toByteArray();
//	}

}
