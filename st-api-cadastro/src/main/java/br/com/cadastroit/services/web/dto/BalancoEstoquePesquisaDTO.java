package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import br.com.cadastroit.services.api.enums.BalancoEstoqueStatus;
import br.com.cadastroit.services.api.enums.BalancoEstoqueStatusLegado;

public class BalancoEstoquePesquisaDTO {

	private Long id;
	private String descricao;
	private Calendar dataCriacao;
	private Calendar dataContagem;
	private Long entidade;
	private String nomeEntidade;
	private String documentoEntidade;
	private Long usuario;
	private String nomeUsuario;
	private BalancoEstoqueStatus novoStatus;
	private String status;
	private boolean zerarEstoque;
	private boolean saldoInicial;
	private BigDecimal totalContado;
	private BigDecimal custoTotal;

	public String getDescricao() {

		return descricao;
	}

	public void setDescricao(String descricao) {

		this.descricao = descricao;
	}

	public Calendar getDataCriacao() {

		return dataCriacao;
	}

	public void setDataCriacao(Calendar dataCriacao) {

		this.dataCriacao = dataCriacao;
	}

	public Calendar getDataContagem() {

		return dataContagem;
	}

	public void setDataContagem(Calendar dataContagem) {

		this.dataContagem = dataContagem;
	}

	public Long getEntidade() {

		return entidade;
	}

	public void setEntidade(Long entidade) {

		this.entidade = entidade;
	}

	public String getNomeEntidade() {

		return nomeEntidade;
	}

	public void setNomeEntidade(String nomeEntidade) {

		this.nomeEntidade = nomeEntidade;
	}

	public String getDocumentoEntidade() {

		return documentoEntidade;
	}

	public void setDocumentoEntidade(String documentoEntidade) {

		this.documentoEntidade = documentoEntidade;
	}

	public Long getUsuario() {

		return usuario;
	}

	public void setUsuario(Long usuario) {

		this.usuario = usuario;
	}

	public String getNomeUsuario() {

		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {

		this.nomeUsuario = nomeUsuario;
	}

	public BalancoEstoqueStatus getNovoStatus() {

		return novoStatus;
	}

	public void setNovoStatus(BalancoEstoqueStatus novoStatus) {

		this.novoStatus = novoStatus;
	}

	public boolean isZerarEstoque() {

		return zerarEstoque;
	}

	public void setZerarEstoque(boolean zerarEstoque) {

		this.zerarEstoque = zerarEstoque;
	}

	public boolean isSaldoInicial() {

		return saldoInicial;
	}

	public void setSaldoInicial(boolean saldoInicial) {

		this.saldoInicial = saldoInicial;
	}

	public BigDecimal getTotalContado() {

		if (totalContado == null)
			totalContado = BigDecimal.ZERO;

		return totalContado;
	}

	public void setTotalContado(BigDecimal totalContado) {

		this.totalContado = totalContado;
	}

	public String getDescricaoStatus() {

		BalancoEstoqueStatus status = this.getNovoStatus();

		if (status == null)
			return StringUtils.EMPTY;

		if (status == BalancoEstoqueStatus.ERRO_EXCLUSAO)
			return BalancoEstoqueStatus.PROCESSADO.getDescricao();

		return status.getDescricao();
	}

	public BigDecimal getCustoTotal() {

		if (custoTotal == null)
			custoTotal = BigDecimal.ZERO;

		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {

		this.custoTotal = custoTotal;
	}

	public String getStatus() {

		return status;
	}

	public void setStatus(String status) {

		this.status = status;
	}

	public boolean isProcessado() {

		return this.getStatus().equals(BalancoEstoqueStatusLegado.AJUSTADO.toString());
	}

	public Boolean getErroExclusao() {

		return this.getNovoStatus() == BalancoEstoqueStatus.ERRO_EXCLUSAO;
	}

}
