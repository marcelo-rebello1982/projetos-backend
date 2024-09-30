package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Calendar;

public class BalancoEstoqueProdutoLoteSerieDTO {

	private Long id;
	private String codigo;
	private BigDecimal quantidade;
	private BigDecimal quantidadeEstoque;
	private Calendar dataFabricacao;
	private Calendar dataVencimento;
	private Long produto;

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

	public BigDecimal getQuantidadeEstoque() {

		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {

		this.quantidadeEstoque = quantidadeEstoque;
	}

	public Calendar getDataFabricacao() {

		return dataFabricacao;
	}

	public void setDataFabricacao(Calendar dataFabricacao) {

		this.dataFabricacao = dataFabricacao;
	}

	public Calendar getDataVencimento() {

		return dataVencimento;
	}

	public void setDataVencimento(Calendar dataVencimento) {

		this.dataVencimento = dataVencimento;
	}

	public Long getProduto() {

		return produto;
	}

	public void setProduto(Long produto) {

		this.produto = produto;
	}

}
