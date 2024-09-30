package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;

import br.com.cadastroit.services.utils.BigDecimalUtils;

public class BalancoEstoqueTotalizadorDTO {

	private Long itens;
	private BigDecimal quantidadeEstoque;
	private BigDecimal quantidadeAferida;
	private BigDecimal valorEstoque;
	private BigDecimal valorEstoqueAferido;
	private boolean zeramento;
	private Long itensEntrada;
	private BigDecimal quantidadeItensEntrada;
	private BigDecimal custoItensEntrada;
	private Long itensSaida;
	private BigDecimal quantidadeItensSaida;
	private BigDecimal custoItensSaida;

	public Long getItens() {

		if (itens == null)
			itens = 0l;

		return itens;
	}

	public void setItens(Long itens) {

		this.itens = itens;
	}

	public BigDecimal getQuantidadeEstoque() {

		if (quantidadeEstoque == null)
			quantidadeEstoque = BigDecimal.ZERO;

		return quantidadeEstoque;
	}

	public void setQuantidadeEstoque(BigDecimal quantidadeEstoque) {

		this.quantidadeEstoque = quantidadeEstoque;
	}

	public BigDecimal getQuantidadeAferida() {

		if (this.isZeramento() || quantidadeAferida == null)
			quantidadeAferida = BigDecimal.ZERO;

		return quantidadeAferida;
	}

	public void setQuantidadeAferida(BigDecimal quantidadeAferida) {

		this.quantidadeAferida = quantidadeAferida;
	}

	public BigDecimal getValorEstoque() {

		if (valorEstoque == null)
			valorEstoque = BigDecimal.ZERO;

		return valorEstoque;
	}

	public void setValorEstoque(BigDecimal valorEstoque) {

		this.valorEstoque = valorEstoque;
	}

	public BigDecimal getValorEstoqueAferido() {

		if (this.isZeramento() || valorEstoqueAferido == null)
			valorEstoqueAferido = BigDecimal.ZERO;

		return valorEstoqueAferido;
	}

	public void setValorEstoqueAferido(BigDecimal valorEstoqueAferido) {

		this.valorEstoqueAferido = valorEstoqueAferido;
	}

	public boolean isZeramento() {

		return zeramento;
	}

	public void setZeramento(boolean zeramento) {

		this.zeramento = zeramento;
	}

	public Long getItensEntrada() {

		if (itensEntrada == null)
			itensEntrada = 0l;

		return itensEntrada;
	}

	public BigDecimal getQuantidadeItensEntrada() {

		return BigDecimalUtils.getZeroIfNull(quantidadeItensEntrada);
	}

	public void setQuantidadeItensEntrada(BigDecimal quantidadeItensEntrada) {

		this.quantidadeItensEntrada = quantidadeItensEntrada;
	}

	public void setItensEntrada(Long itensEntrada) {

		this.itensEntrada = itensEntrada;
	}

	public BigDecimal getCustoItensEntrada() {

		return BigDecimalUtils.getZeroIfNull(custoItensEntrada);
	}

	public void setCustoItensEntrada(BigDecimal custoItensEntrada) {

		this.custoItensEntrada = custoItensEntrada;
	}

	public Long getItensSaida() {

		if (itensSaida == null)
			itensSaida = 0l;

		return itensSaida;
	}

	public void setItensSaida(Long itensSaida) {

		this.itensSaida = itensSaida;
	}

	public BigDecimal getQuantidadeItensSaida() {

		return BigDecimalUtils.getZeroIfNull(quantidadeItensSaida);
	}

	public void setQuantidadeItensSaida(BigDecimal quantidadeItensSaida) {

		this.quantidadeItensSaida = quantidadeItensSaida;
	}

	public BigDecimal getCustoItensSaida() {

		return BigDecimalUtils.getZeroIfNull(custoItensSaida);
	}

	public void setCustoItensSaida(BigDecimal custoItensSaida) {

		this.custoItensSaida = custoItensSaida;
	}

}
