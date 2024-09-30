package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import br.com.cadastroit.services.utils.BigDecimalUtils;

public class BalancoEstoqueProdutoDTO {

	private Long id;
	private Long balancoEstoque;
	private Long produto;
	private String nome;
	private String codigo;
	private Long unidade;
	private String siglaUnidadeMedida;
	private int casasDecimaisQuantidade;
	private BigDecimal quantidade;
	private BigDecimal quantidadeEstoque;
	private BigDecimal valorCustoMedio;
	private List<BalancoEstoqueProdutoLoteSerieDTO> lotesSeries;
	private boolean lote;
	private boolean serie;
	private boolean produtoPermiteEpcs;
	private List<String> epcs;

	public Long getBalancoEstoque() {

		return balancoEstoque;
	}

	public void setBalancoEstoque(Long balancoEstoque) {

		this.balancoEstoque = balancoEstoque;
	}

	public Long getProduto() {

		return produto;
	}

	public void setProduto(Long produto) {

		this.produto = produto;
	}

	public String getNome() {

		return nome;
	}

	public void setNome(String nome) {

		this.nome = nome;
	}

	public String getCodigo() {

		return codigo;
	}

	public void setCodigo(String codigo) {

		this.codigo = codigo;
	}

	public Long getUnidade() {

		return unidade;
	}

	public void setUnidade(Long unidade) {

		this.unidade = unidade;
	}

	public String getSiglaUnidadeMedida() {

		return siglaUnidadeMedida;
	}

	public void setSiglaUnidadeMedida(String siglaUnidadeMedida) {

		this.siglaUnidadeMedida = siglaUnidadeMedida;
	}

	public int getCasasDecimaisQuantidade() {

		return casasDecimaisQuantidade;
	}

	public void setCasasDecimaisQuantidade(int casasDecimaisQuantidade) {

		this.casasDecimaisQuantidade = casasDecimaisQuantidade;
	}

	public BigDecimal getQuantidade() {

		return BigDecimalUtils.zeroIfNull(quantidade);
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

	public BigDecimal getValorCustoMedio() {

		return valorCustoMedio;
	}

	public void setValorCustoMedio(BigDecimal valorCustoMedio) {

		this.valorCustoMedio = valorCustoMedio;
	}

	public List<BalancoEstoqueProdutoLoteSerieDTO> getLotesSeries() {

		if (lotesSeries == null)
			lotesSeries = new ArrayList<>();

		return lotesSeries;
	}

	public void setLotesSeries(List<BalancoEstoqueProdutoLoteSerieDTO> lotesSeries) {

		this.lotesSeries = lotesSeries;
	}

	public boolean isLote() {

		return lote;
	}

	public void setLote(boolean lote) {

		this.lote = lote;
	}

	public boolean isSerie() {

		return serie;
	}

	public void setSerie(boolean serie) {

		this.serie = serie;
	}

	public void addQuantidade(BigDecimal quantidade) {

		BigDecimal novaQuantidade = this.getQuantidade().add(quantidade);
		this.setQuantidade(novaQuantidade);
	}

	public String getIdentificadorProduto() {

		if (this.getProduto() != null)
			return this.getProduto().toString();

		return this.getCodigo();
	}

	public boolean isProdutoPermiteEpcs() {

		return produtoPermiteEpcs;
	}

	public void setProdutoPermiteEpcs(boolean produtoPermiteEpcs) {

		this.produtoPermiteEpcs = produtoPermiteEpcs;
	}

	public List<String> getEpcs() {

		if (epcs == null)
			epcs = new ArrayList<>();

		return epcs;
	}

	public void setEpcs(List<String> epcs) {

		this.epcs = epcs;
	}

	public void addEpcsAndUpdateQuantidade(Collection<String> epcsToAdd) {

		if (CollectionUtils.isEmpty(epcsToAdd))
			return;

		Set<String> epcs = new HashSet<>(this.getEpcs());
		epcs.addAll(epcsToAdd);

		this.setEpcs(new ArrayList<>(epcs));
		this.setQuantidade(new BigDecimal(epcs.size()));
	}

}
