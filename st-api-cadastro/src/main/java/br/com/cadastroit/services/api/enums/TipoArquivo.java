package br.com.cadastroit.services.api.enums;
public enum TipoArquivo {

	PDF("application/pdf", "pdf"),
	XLSX("application/octet-stream", "xls"),
	HTML("text/html", "html"),
	TXT("plain/text", "txt"),
	PNG("image/png", "png");

	private String contentType;
	private String extensao;

	TipoArquivo(String contentType, String extensao) {

		this.contentType = contentType;
		this.extensao = extensao;
	}

	public String getContentType() {

		return contentType;
	}

	public String getExtensao() {

		return extensao;
	}

	public boolean isXlsx() {

		return this == XLSX;
	}

}
