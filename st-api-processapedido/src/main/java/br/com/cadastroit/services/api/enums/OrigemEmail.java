package br.com.cadastroit.services.api.enums;

public enum OrigemEmail {

	EMISSAO_PROPRIA("EMISSAO_PROPRIA"),
	TERCEIRO("LEGADO"),
	LEGADO("LEGADO");

	// OrigemEmail.EMISSAO_PROPRIA.name(); // Retorna "EMISSAO_PROPRIA"

	public final String origemEmail;

	private OrigemEmail(String origemEmail) {

		this.origemEmail = origemEmail;
	}

	public String getOrigemEmail() {

		return this.origemEmail;
	}
}