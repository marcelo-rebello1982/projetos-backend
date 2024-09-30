package br.com.complianceit.enterprise.common.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MaskFormatter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class UtilString {
	
	public static String arrayToString(Object[] array){
		String result = "";
		int count = 0;
		for(Object o : array){
			result += count == 0 ? "["+count+"]"+" = "+o.toString() : ",["+count+"]"+" = "+o.toString();
			count++;
		}
		return result;
	}
	
	public static String lpadS(String valueToPad, String filler, int size) {
		StringBuilder builder = new StringBuilder();
		
		while (builder.length() + valueToPad.length() < size) {
			builder.append(filler);
		}
		builder.append(valueToPad);
		return builder.toString();
	}
	
	public static String lpad(String valueToPad, char filler, int size) {
		char[] array = new char[size];
		
		int len = size - valueToPad.length();
		
		for (int i = 0; i < len; i++)
			array[i] = filler;
		
		valueToPad.getChars(0, valueToPad.length(), array, size - valueToPad.length());
		
		return String.valueOf(array);
	}
	
	public static String formataCNPJ(String cnpj) {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.substring(cnpj, 0, 2)).append(".");
		sb.append(StringUtils.substring(cnpj, 2, 5)).append(".");
		sb.append(StringUtils.substring(cnpj, 5, 8)).append("/");
		sb.append(StringUtils.substring(cnpj, 8, 12)).append("-");
		sb.append(StringUtils.substring(cnpj, 12, 14));
		return sb.toString();
	}
	
	public static BigDecimal[] quebraCNPJ(String cnpj) {
		String _cnpj = StringUtils.leftPad(cnpj, 14, "0");
		BigDecimal numCnpj = new BigDecimal(StringUtils.substring(_cnpj, 0, 8));
		BigDecimal numFilial = new BigDecimal(StringUtils.substring(_cnpj, 8, 12));
		BigDecimal digCnpj = new BigDecimal(StringUtils.substring(_cnpj, 12, 14));
		return new BigDecimal[] {numCnpj,numFilial,digCnpj};
	}
	
	public static String formataCPF(String cpf) {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.substring(cpf, 0, 3)).append(".");
		sb.append(StringUtils.substring(cpf, 3, 6)).append(".");
		sb.append(StringUtils.substring(cpf, 6, 9)).append("-");
		sb.append(StringUtils.substring(cpf, 9, 11));
		return sb.toString();
	}
	
	public static String formatString(String texto, String mascara) throws ParseException {
		MaskFormatter mf = new MaskFormatter(mascara);
		mf.setValueContainsLiteralCharacters(false);
		return mf.valueToString(texto);
	}
	
	public static String substRPad(String s, int tamanho) {
		return StringUtils.rightPad(StringUtils.substring(s, 0, tamanho), tamanho);
	}
	
	// Metodo privado para auxiliar envio de email geral ou individual
	public static String[] splitEnderecoEmail(String acaoEmailCustom){
		String[] emails = null;
		if (acaoEmailCustom != null && acaoEmailCustom.length() > 0) {
			acaoEmailCustom = acaoEmailCustom.trim().replace("\n", "").replace(" ", "").replace(",", ";");
			if(acaoEmailCustom.contains(";")){
				emails = acaoEmailCustom.split(";");
			}else{
				emails = (String[]) ArrayUtils.add(emails, acaoEmailCustom);
			}
		}
		return emails;
	}
	
	public static String textoCceCondUso(){
		return "A Carta de Correcao e disciplinada pelo paragrafo 1o-A do art. 7o do Convenio S/N, de 15 de dezembro de 1970 e pode ser utilizada para regularizacao de erro ocorrido na emissao de documento fiscal, desde que o erro nao esteja relacionado com: I - as variaveis que determinam o valor do imposto tais como: base de calculo, aliquota, diferenca de preco, quantidade, valor da operacao ou da prestacao; II - a correcao de dados cadastrais que implique mudanca do remetente ou do destinatario; III - a data de emissao ou de saida.";
	}

	@SuppressWarnings("unused")
	public static String acertaTagXml(String xml){
		String regex = "(<[^/].[^(><.)]+>)";
		String xxx = xml+"";
		Pattern p = Pattern.compile(regex);
		List<String> matches = new ArrayList<String>();
	    Matcher lm = p.matcher(xml); // Line matcher
	    Matcher pm = null; // Pattern matcher
	    int lines = 0;
	    while (lm.find()) {
	      lines++;
	      CharSequence cs = lm.group(); // The current line
	      if (pm == null)
	        pm = p.matcher(cs);
	      else
	        pm.reset(cs);
	      if (pm.find()) {
	    	  String aux = cs.toString().replace("<", "</");
	    	  if(!xxx.contains(aux)){	    		  
	  	         matches.add(aux);
	    	  }
	    	  xxx = xxx.replaceFirst(cs.toString(), "").replaceFirst(aux, "");
	      }
	      if (lm.end() == xml.length())
	        break;
	    }
	    for(int i=matches.size()-1;  i!=-1 ; i--){
	    	xml = xml.concat(matches.get(i).toString());
	    }
	    return xml;
	}
	
	public static String subStringMaxLenght(String value, int maxLenght){
		if(value != null && !value.isEmpty()){
			return value.length() <= maxLenght ? value.substring(0, value.length()) : value.substring(0, (maxLenght - 1));
		}
		return null;		
	}
	
}
