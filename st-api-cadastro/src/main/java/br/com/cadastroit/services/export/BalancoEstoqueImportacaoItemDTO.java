package br.com.cadastroit.services.export;
import java.math.BigDecimal;

import br.com.cadastroit.services.export.excel.BigDecimalImportTxtFormatter;
import br.com.cadastroit.services.export.excel.ImportTxtColumn;
import br.com.cadastroit.services.web.dto.ImportacaoArquivoAbstractDTO;


public class BalancoEstoqueImportacaoItemDTO extends ImportacaoArquivoAbstractDTO {

	@ImportTxtColumn(ordem = 0)
	private String codigo;

	@ImportTxtColumn(ordem = 10, formatter = BigDecimalImportTxtFormatter.class)
	private BigDecimal quantidade = BigDecimal.ONE;

	public String getCodigo() {

		return codigo;
	}

	public void setCodigo(String codigo) {

		this.codigo = codigo;
	}

	public BigDecimal getQuantidade() {

		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {

		this.quantidade = quantidade;
	}

	public void addQuantidade(BigDecimal quantidade) {

		this.quantidade = this.getQuantidade().add(quantidade);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("BalancoEstoqueImportacaoItemDTO: {codigo: ")
				.append(codigo)
				.append(", quantidade: ")
				.append(quantidade)
				.append(", getLinha(): ")
				.append(getLinha())
				.append("}");
		return builder.toString();
	}

}