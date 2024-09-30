package br.com.cadastroit.services.mail.enums;

public enum NotaFiscalEnum {

	NAO_VALIDADA(0),
	NAO_PROCESSADA(1),
	PROCESSADA(2),
	AGUARDANDO_RETORNO(3),
	AUTORIZADA(4),
    REJEITADA(5),
    DENEGADA(6),
    CANCELADA(7),
    INUTILIZADA(8),
    CONC_EPEC_REJEITADA(9),
    ERRO_VALIDACAO_NOTA(10),
    ERRO_MONTAGEM_XML(11),
    ERRO_ENVIO_SEFAZ(12),
    ERRO_RETORNO_ENVIO(13),
    CONTINGENCIA(14),
    ERRO_CANCELAMENTO(15),
    ERRO_INUTILIZACAO(16),
    DIGITADA_NFS(18),
    PROCESSADA_NFS(19),
    ERRO_GERAL(99),
    RPS_NAO_CONVERTIDO(20),
    AGUARDANDO_LIBERACAO(21),
    AGUARDANDO_CORRECAO(22),
    INTEGRA_INDEF(0),
    INTEGRA_BD(1),
    INTEGRA_TXT_IN(2),
    INTEGRA_TXT_OUT(3),
    INTEGRA_ERR_TXT(4),
    INTEGRA_TXT_ENT(5),
    INTEGRA_TXT_LEG(6),
    INTEGRA_SOFTWAY_IN(13),
    INTEGRA_SOFTWAY_OUT(14);
	
	public final Integer dmStProc;
	
	private NotaFiscalEnum(Integer dmStProc) {
		this.dmStProc = dmStProc;
	}
	
	public Integer dmStProc() {
		return this.dmStProc;
	}
}
