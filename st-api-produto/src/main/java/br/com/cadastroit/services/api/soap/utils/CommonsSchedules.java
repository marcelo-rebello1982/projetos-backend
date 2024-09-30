package br.com.cadastroit.services.api.soap.utils;
import java.util.Date;

public class CommonsSchedules {

	public static final String REQUEST = "Request";
	public static final String RESPONSE = "Response";

	protected static String GET_HEADER() {
		StringBuilder header = new StringBuilder();
		header.append("<ns2:cabecalho versao=\"3\" xmlns:ns2=\"http://www.ginfes.com.br/cabecalho_v03.xsd\"><versaoDados>3</versaoDados></ns2:cabecalho>");
		return header.toString();
	}

	public static void WAIT(long millseconds) {
		Date date = new Date();
		Long timeAtual = date.getTime();
		Long tenSecondsWait = timeAtual + millseconds;
		boolean continueExec = true;
		while (continueExec) {
			Date dateInt = new Date();
			if (dateInt.getTime() > tenSecondsWait) {
				continueExec = false;
			}
		}
	}

	protected static String GET_SOAP_ENV(String nameSpace, String method, String xml) {
		StringBuilder env = new StringBuilder();
		env.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:impl=\""
				+ nameSpace + "\">").append("<soapenv:Header/>").append("<soapenv:Body>")
				.append("<impl:" + method + REQUEST + ">")
				.append("<nfseCabecMsg><![CDATA[" + CommonsSchedules.GET_HEADER() + "]]></nfseCabecMsg>")
				.append("<nfseDadosMsg><![CDATA[" + xml + "]]></nfseDadosMsg>")
				.append("</impl:" + method + REQUEST + ">").append("</soapenv:Body>").append("</soapenv:Envelope>");
		return env.toString();
	}

	public static String UNWRAP_SOAP(String soapMessage, String param1, String param2) {
		String fixedXml = FIX_XML(soapMessage);
		int topIndex = fixedXml.lastIndexOf(param1);
		int downIndex = fixedXml.lastIndexOf(param2);
		return fixedXml.substring(topIndex, downIndex);
	}

	public static String FIX_XML(String xml) {
		return xml.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"");
	}
}