package br.com.cadastroit.services.web.dto;
import lombok.ToString;

@ToString
public class ImportacaoArquivoAbstractDTO {

	private int linha;

	public int getLinha() {

		return linha;
	}

	public void setLinha(int linha) {

		this.linha = linha;
	}

}