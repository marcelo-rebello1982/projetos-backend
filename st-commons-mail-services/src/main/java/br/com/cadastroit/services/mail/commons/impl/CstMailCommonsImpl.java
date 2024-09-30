package br.com.cadastroit.services.mail.commons.impl;

public interface CstMailCommonsImpl {
	
	Integer updateOracle = System.getenv("UPDATE_ORACLE") != null ? Integer.parseInt(System.getenv("UPDATE_ORACLE")) : 0;
	Integer sendEmail	 = System.getenv("SEND_EMAIL") != null ? Integer.parseInt(System.getenv("SEND_EMAIL")) : 0;
	Integer sendMailTerc = System.getenv("SEND_MAIL_TERC") == null ? 0 : Integer.parseInt(System.getenv("SEND_MAIL_TERC"));
	Integer sendMailLeg  = System.getenv("SEND_MAIL_LEG") == null ? 0 : Integer.parseInt(System.getenv("SEND_MAIL_LEG"));

	String DESATIVAR_ENVIO = "Descartando envio de email para NOTAFISCAL[ID] = %s. Envio de email %s inativo";
	String ANEXANDO_DOCUMENTO = "Anexando dados da nota (%s) ===> %s";
	String ENVIANDO_EMAIL     = "Iniciando envio do email para nota ===> %s - From Address ===> %s - Assunto ===> %s";
	String ERROR_ENVIAR_EMAIL = "Erro ao enviar email da nota %s : %s";
	String EMAIL_ENVIADO      = "Email enviado para nota ===> %s";
	String[] ignoredErrors = {"Nao existem credenciais de SMTP cadastradas", 
	  						  "554 Message rejected: Email address is not verified."};
}
