package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.cadastroit.services.api.enums.MetodoControle;
import br.com.cadastroit.services.utils.BigDecimalUtils;
import br.com.cadastroit.services.utils.StringUtils;

public class InventarioConsultaResultadoDTO {

	private static final int PRECISAO_MONETARIA = 4;

	private BigDecimal quantidade;

	private BigDecimal reservado;

	private Long idMercadoria;

	private Long idEntidade;

	private Long idLote;

	private String nomeMercadoria;

	private String classificacaoMercadoria;

	private boolean mercadoriaAtiva;

	private MetodoControle metodoControle;

	private BigDecimal estoqueMinimo;

	private BigDecimal estoqueMaximo;

	private Long idUnidade;

	private String nomeUnidade;

	private String siglaUnidade;

	private int casasDecimaisUnidade;

	private String codigoSistema;

	private String codigoInterno;

	private String codigoBarras;

	private Calendar vencimentoLote;

	private Calendar fabricacaoLote;

	private String lote;

	private BigDecimal precoVenda;

	private BigDecimal custoUltimaCompra;

	private BigDecimal custoReferencial;

	private BigDecimal custoMedio;

	private int casasDecimaisCusto;

	private int casasDecimaisVenda;

	private boolean precoVariavel;

	private Calendar data;

	private Long idCategoria;

	private String categoriasNomeFormatado;

	private String ncm;

	private Long idContaEstoque;

	private String especificacaoMercadoria;

	private BigDecimal precoVendaTabelaFiltrada;

	private String ultimoFornecedorNome;

	public BigDecimal getQuantidade() {

		if (quantidade == null)
			quantidade = BigDecimal.ZERO;

		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {

		this.quantidade = quantidade;
	}

	public BigDecimal getReservado() {

		return reservado;
	}

	public void setReservado(BigDecimal reservado) {

		this.reservado = reservado;
	}

	public BigDecimal getValorEstoque() {

		BigDecimal custoMedio = this.getCustoReferencialIfCustoMedioZero();
		BigDecimal quantidade = this.getQuantidade();

		return custoMedio.multiply(quantidade);
	}

	public Long getIdMercadoria() {

		return idMercadoria;
	}

	public void setIdMercadoria(Long idMercadoria) {

		this.idMercadoria = idMercadoria;
	}

	public Long getIdEntidade() {

		return idEntidade;
	}

	public void setIdEntidade(Long idEntidade) {

		this.idEntidade = idEntidade;
	}

	public Long getIdLote() {

		return idLote;
	}

	public void setIdLote(Long idLote) {

		this.idLote = idLote;
	}

	public String getNomeMercadoria() {

		return nomeMercadoria;
	}

	public void setNomeMercadoria(String nomeMercadoria) {

		this.nomeMercadoria = nomeMercadoria;
	}

	public String getClassificacaoMercadoria() {

		return classificacaoMercadoria;
	}

	public void setClassificacaoMercadoria(String classificacaoMercadoria) {

		this.classificacaoMercadoria = classificacaoMercadoria;
	}

	public boolean isMercadoriaAtiva() {

		return mercadoriaAtiva;
	}

	public void setMercadoriaAtiva(boolean mercadoriaAtiva) {

		this.mercadoriaAtiva = mercadoriaAtiva;
	}

	public MetodoControle getMetodoControle() {

		return metodoControle;
	}

	public void setMetodoControle(MetodoControle metodoControle) {

		this.metodoControle = metodoControle;
	}

	public BigDecimal getEstoqueMinimo() {

		if (estoqueMinimo == null)
			estoqueMinimo = BigDecimal.ZERO;

		return estoqueMinimo;
	}

	public void setEstoqueMinimo(BigDecimal estoqueMinimo) {

		this.estoqueMinimo = estoqueMinimo;
	}

	public BigDecimal getEstoqueMaximo() {

		if (estoqueMaximo == null)
			estoqueMaximo = BigDecimal.ZERO;

		return estoqueMaximo;
	}

	public void setEstoqueMaximo(BigDecimal estoqueMaximo) {

		this.estoqueMaximo = estoqueMaximo;
	}

	public Long getIdUnidade() {

		return idUnidade;
	}

	public void setIdUnidade(Long idUnidade) {

		this.idUnidade = idUnidade;
	}

	public String getNomeUnidade() {

		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {

		this.nomeUnidade = nomeUnidade;
	}

	public String getSiglaUnidade() {

		return siglaUnidade;
	}

	public void setSiglaUnidade(String siglaUnidade) {

		this.siglaUnidade = siglaUnidade;
	}

	public int getCasasDecimaisUnidade() {

		return casasDecimaisUnidade;
	}

	public void setCasasDecimaisUnidade(int casasDecimaisUnidade) {

		this.casasDecimaisUnidade = casasDecimaisUnidade;
	}

	public String getCodigoSistema() {

		return codigoSistema;
	}

	public void setCodigoSistema(String codigoSistema) {

		this.codigoSistema = codigoSistema;
	}

	public String getCodigoInterno() {

		return codigoInterno;
	}

	public void setCodigoInterno(String codigoInterno) {

		this.codigoInterno = codigoInterno;
	}

	public String getCodigoBarras() {

		return codigoBarras;
	}

	public void setCodigoBarras(String codigoBarras) {

		this.codigoBarras = codigoBarras;
	}

	public Calendar getVencimentoLote() {

		return vencimentoLote;
	}

	public void setVencimentoLote(Calendar vencimentoLote) {

		this.vencimentoLote = vencimentoLote;
	}

	public Calendar getFabricacaoLote() {

		return fabricacaoLote;
	}

	public void setFabricacaoLote(Calendar fabricacaoLote) {

		this.fabricacaoLote = fabricacaoLote;
	}

	public String getLote() {

		if (lote == null)
			lote = StringUtils.EMPTY;

		return lote;
	}

	public void setLote(String lote) {

		this.lote = lote;
	}

	public BigDecimal getPrecoVenda() {

		if (precoVenda == null)
			precoVenda = BigDecimal.ZERO;

		return precoVenda;
	}

	public void setPrecoVenda(BigDecimal precoVenda) {

		this.precoVenda = precoVenda;
	}

	public BigDecimal getCustoUltimaCompra() {

		if (custoUltimaCompra == null)
			custoUltimaCompra = BigDecimal.ZERO;

		return custoUltimaCompra;
	}

	public void setCustoUltimaCompra(BigDecimal custoUltimaCompra) {

		this.custoUltimaCompra = custoUltimaCompra;
	}

	public BigDecimal getCustoReferencial() {

		if (custoReferencial == null)
			custoReferencial = BigDecimal.ZERO;

		return custoReferencial;
	}

	public void setCustoReferencial(BigDecimal custoReferencial) {

		this.custoReferencial = custoReferencial;
	}

	public BigDecimal getCustoMedio() {

		if (custoMedio == null)
			custoMedio = this.getCustoReferencial();

		return custoMedio;
	}

	public void setCustoMedio(BigDecimal custoMedio) {

		this.custoMedio = custoMedio;
	}

	public int getCasasDecimaisCusto() {

		if (casasDecimaisCusto == 0)
			casasDecimaisCusto = PRECISAO_MONETARIA;

		return casasDecimaisCusto;
	}

	public void setCasasDecimaisCusto(int casasDecimaisCusto) {

		this.casasDecimaisCusto = casasDecimaisCusto;
	}

	public int getCasasDecimaisVenda() {

		if (casasDecimaisVenda == 0)
			casasDecimaisVenda = PRECISAO_MONETARIA;

		return casasDecimaisVenda;
	}

	public void setCasasDecimaisVenda(int casasDecimaisVenda) {

		this.casasDecimaisVenda = casasDecimaisVenda;
	}

	public boolean isPrecoVariavel() {

		return precoVariavel;
	}

	public void setPrecoVariavel(boolean precoVariavel) {

		this.precoVariavel = precoVariavel;
	}

	public Calendar getData() {

		return data;
	}

	public void setData(Calendar data) {

		this.data = data;
	}

	public Long getIdCategoria() {

		return idCategoria;
	}

	public void setIdCategoria(Long idCategoria) {

		this.idCategoria = idCategoria;
	}

	public String getCategoriasNomeFormatado() {

		return categoriasNomeFormatado;
	}

	public void setCategoriasNomeFormatado(String categoriasNomeFormatado) {

		this.categoriasNomeFormatado = categoriasNomeFormatado;
	}

	public String getNcm() {

		return ncm;
	}

	public void setNcm(String ncm) {

		this.ncm = ncm;
	}

	public Long getIdContaEstoque() {

		return idContaEstoque;
	}

	public void setIdContaEstoque(Long idContaEstoque) {

		this.idContaEstoque = idContaEstoque;
	}

	public String getEspecificacaoMercadoria() {

		return especificacaoMercadoria;
	}

	public void setEspecificacaoMercadoria(String especificacaoMercadoria) {

		this.especificacaoMercadoria = especificacaoMercadoria;
	}

	public BigDecimal getPrecoVendaTabelaFiltrada() {

		if (precoVendaTabelaFiltrada == null)
			precoVendaTabelaFiltrada = BigDecimal.ZERO;

		return precoVendaTabelaFiltrada;
	}

	public void setPrecoVendaTabelaFiltrada(BigDecimal precoVendaTabelaFiltrada) {

		this.precoVendaTabelaFiltrada = precoVendaTabelaFiltrada;
	}

	public BigDecimal getTotalPrecoVenda() {

		return this.getPrecoVenda().multiply(this.getQuantidade());
	}

	public BigDecimal getTotalCustoUltimaCompra() {

		return this.getCustoUltimaCompra().multiply(this.getQuantidade());
	}

	public BigDecimal getTotalCustoReferencial() {

		return this.getCustoReferencial().multiply(this.getQuantidade());
	}

	public BigDecimal getTotalCustoMedio() {

		return this.getCustoMedio().multiply(this.getQuantidade());
	}

	public String getCodigo() {

		// return Produto.codigoPrecedencia(this.getCodigoInterno(), this.getCodigoBarras(), this.getCodigoSistema());
		return "";
	}

	public String getDescricaoMercadoria() {

		if (StringUtils.isEmpty(this.getLote()))
			return this.getNomeMercadoria();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		return this.getNomeMercadoria() + "\r\n" + "Lote: " + this.getLote() + " - Validade: "
				+ dateFormat.format(this.getVencimentoLote().getTime());

	}

	public boolean isLote() {

		return this.getMetodoControle() != null && this.getMetodoControle().equals(MetodoControle.LOTE);
	}

	public boolean isSerie() {

		return this.getMetodoControle() != null && this.getMetodoControle().equals(MetodoControle.SERIE);
	}

	public BigDecimal getCustoReferencialIfCustoMedioZero() {

		if (this.custoMedio == null || BigDecimalUtils.isZero(this.custoMedio))
			return this.getCustoReferencial();

		return this.custoMedio;
	}

	public String getUltimoFornecedorNome() {

		return ultimoFornecedorNome;
	}

	public void setUltimoFornecedorNome(String ultimoFornecedorNome) {

		this.ultimoFornecedorNome = ultimoFornecedorNome;
	}

}
