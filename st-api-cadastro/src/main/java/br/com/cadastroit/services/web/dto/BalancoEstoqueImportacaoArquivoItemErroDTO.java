package br.com.cadastroit.services.web.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import br.com.cadastroit.services.export.BalancoEstoqueImportacaoItemDTO;

public class BalancoEstoqueImportacaoArquivoItemErroDTO extends BalancoEstoqueImportacaoItemDTO {

	private Long produto;
	private String nome;
	private String unidadeMedida;
	private int casasDecimaisQuantidade;
	private String mensagem;
	private List<String> epcs;

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

	public String getUnidadeMedida() {

		return unidadeMedida;
	}

	public void setUnidadeMedida(String unidadeMedida) {

		this.unidadeMedida = unidadeMedida;
	}

	public int getCasasDecimaisQuantidade() {

		return casasDecimaisQuantidade;
	}

	public void setCasasDecimaisQuantidade(int casasDecimaisQuantidade) {

		this.casasDecimaisQuantidade = casasDecimaisQuantidade;
	}

	public String getMensagem() {

		return mensagem;
	}

	public void setMensagem(String mensagem) {

		this.mensagem = mensagem;
	}

	public String getIdentificadorProduto() {

		if (this.getProduto() != null)
			return this.getProduto().toString();

		return this.getCodigo();
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
