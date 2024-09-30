package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;

public class BalancoEstoqueProdutoZeramentoDTO extends BalancoEstoqueProdutoDTO {

	@Override
	public BigDecimal getQuantidade() {

		return super.getQuantidadeEstoque().negate();
	}

}