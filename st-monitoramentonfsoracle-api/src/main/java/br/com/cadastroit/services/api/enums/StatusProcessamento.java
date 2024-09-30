package br.com.cadastroit.services.api.enums;

public enum StatusProcessamento {
	
	 //https://docs.oracle.com/javase/1.5.0/docs/guide/language/enums.html

	VALIDADA(1, "Validada, Aguardando Processamento"),
	PROCESSADA(2, " Processada, Aguardando Envio"),
	ENVIADA(3, "Enviada, Aguardando Retorno"),
	AUTORIZADA(4, "Autorizada"),
	REJEITADA(5, "Rejeitada"),
	ERRONAVALIDACAO(10, "Erro na Validacao"),
	ERRONAMONTAGEMDOXML(11, "Erro na Montagem do XML"),
	ERROAOENVIARANOTA(12, "Erro ao enviar a Nota"),
	ERROAOOBTERORETORNODOENVIODANOTA(13, "Erro ao obter o retorno do envio da Nota"),
	RPSNAOCONVERTIDO(20, "RPS não Convertido"),
	AGUARDANDOLIBERACAO(21, "Aguardando Liberacao"),
	SUBSTITUIDA(23, "Substituída"),
	ERROGERALDESISTEMA(99, "Erro Geral de Sistema");
	
	private String values;
	private Integer keys;
	
	private StatusProcessamento(Integer key, String value) {
		keys = key;
		values = value;
	}

	public Integer getKeys() {
		return keys;
	}

	public String getValues() {
		return values;
	}
}

