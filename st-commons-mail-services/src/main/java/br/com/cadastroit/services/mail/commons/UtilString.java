package br.com.cadastroit.services.mail.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MaskFormatter;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

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
	
	public static String applyPatternEnUS(String pattern, Object value) {
        Locale loc = new Locale("en", "US");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(loc);
        DecimalFormat decFormat = (DecimalFormat) numberFormat;
        decFormat.applyPattern(pattern);//Aplicando o pattern
        return decFormat.format(value);//Retornando o valor
    }
    
    public static String toCurrency(BigDecimal vl, boolean applyCoin) {
        if(applyCoin){
            return toCurrency(vl);
        }else{
            return toCurrency(vl).replace("R$ ", "").replace(".", "");
        }        
    }

    public static String toCurrency(BigDecimal vl) {
        NumberFormat currencyFormat;
        Locale localeBrazil = new Locale("pt", "BR");
        currencyFormat = NumberFormat.getCurrencyInstance(localeBrazil);
        return currencyFormat.format(vl);
    }

    public static BigDecimal toBigDecimal(String vl) {
        if (vl != null && vl.length() > 0) {
            return new BigDecimal(vl);
        } else {
            return null;
        }
    }

    public static Long toLong(String vl) {
        if (vl != null && vl.length() > 0) {
            return new Long(vl);
        } else {
            return null;
        }
    }

    public static BigDecimal toBigDecimal(String vl, int scale) {
        if (vl != null && vl.length() > 0) {
            BigDecimal b = new BigDecimal(vl);
            b.setScale(scale);
            return b;
        } else {
            return null;
        }
    }

    public static String toString(Number vl, int scale) {
        if (vl != null) {
            String precision = "0.";
            int i = 0;
            while (i < scale) {
                precision = precision + "0";
                i++;
            }
            DecimalFormat fmt = new DecimalFormat(precision);
            return fmt.format(vl);
        } else {
            return null;
        }
    }

    public static BigDecimal casasDecimais(int casas, BigDecimal valor) {
        String quantCasas = "%." + casas + "f", textoValor = "0";
        if (valor != null){
	        try {
	            textoValor = String.format(Locale.getDefault(), quantCasas, valor);
	        } catch (IllegalArgumentException e) {
	            if (e.getMessage().equals("Digits < 0")) {
	                textoValor = "0.0000";
	            }
	            System.out.println(e.getMessage());
	        }
	        return new BigDecimal(textoValor.replace(",", "."));
        }else{
        	return null;
        }
    }

    public static String toStringEn(Number vl, int scale) {
        if (vl != null) {
            String precision = "0.";
            int i = 0;
            while (i < scale) {
                precision = precision + "0";
                i++;
            }
            DecimalFormat fmt = new DecimalFormat(precision, new DecimalFormatSymbols(Locale.ENGLISH));
            return fmt.format(vl);
        } else {
            return null;
        }
    }

    public static String toStringEnRounding(Number vl, int scale) {
        if (vl != null) {
            String precision = "0.";
            int i = 0;
            while (i < scale) {
                precision = precision + "0";
                i++;
            }
            DecimalFormat fmt = new DecimalFormat(precision, new DecimalFormatSymbols(Locale.ENGLISH));
            fmt.setRoundingMode(RoundingMode.HALF_UP);
            return fmt.format(vl);
        } else {
            return null;
        }
    }
    
    public static Date getParsedDate(String value) throws Exception {
		String[] formats = new String[] {
			"yyyy-MM-dd'T'HH:mm:ss",
			"yyyy-MM-dd"
		};
		for (String format : formats) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("pt", "BR"));
				String date = value.toString();
				Date data = (!date.equals("")) ? formatter.parse(date) : null;
				return data;
			} catch (ParseException e) {}
		}
		throw new Exception("Erro de conversao Date [" + value + "].");
	}

	public static Timestamp getParsedTimestamp(String value) throws Exception {
		String[] formats = new String[] {
			"yyyy-MM-dd'T'HH:mm:ss",
			"yyyy-MM-dd"
		};
		for (String format : formats) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("pt", "BR"));
				String date = value.toString();
				Date parsedDate = (!date.equals("")) ? formatter.parse(date) : null;
				Timestamp data = (parsedDate != null) ? new Timestamp(parsedDate.getTime()) : null;
				return data;
			} catch (ParseException e) {}
		}
		throw new Exception("Erro de conversao Timestamp [" + value + "].");
	}

	public static String getDateTime(Long timeAdd){
		Date d 		= new Date();
		Long time 	= d.getTime();
		Long newTime= time+timeAdd;
		
		String date = toDateTimeString(new Timestamp(newTime));
		return date;
	}
	public static String getDateTimeNow(){
		return UtilString.toDateTimeString(new Timestamp(new Date().getTime()));
	}
	
	public static Date toDateTimeSAP(String dateTimestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (dateTimestamp != null) {
			try {
				return sdf.parse(dateTimestamp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Date toDateTime(String dateTimestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		if (dateTimestamp != null) {
			try {
				return sdf.parse(dateTimestamp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Date toDateFromTimestamp(String dateTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dateTimestamp != null) {
            try {
                return sdf.parse(dateTimestamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
	
	public static Date toDateTimeDb(String dateTimestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (dateTimestamp != null) {
			try {
				return sdf.parse(dateTimestamp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String toDateTimeString(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (timestamp != null) {
			return sdf.format(timestamp);
		}
		return "";
	}
	
	public static String toDateTimeStringTextPlain(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (timestamp != null) {
			return sdf.format(timestamp);
		}
		return "";
	}
	
	public static Date toDateTimeStringUTC(String date) {
		Date dateParse = toDateFromXmlDate(date);
		return dateParse;
	}
	
	public static String toDateTimeStringUTC(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String date = sdf.format(timestamp);
		return date.substring(0, 22)+":"+date.substring(22, 24);
	}
	
	public static String toDateTimeStringWithoutSeconds(Timestamp timestamp){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		if (timestamp != null) {
			return sdf.format(timestamp);
		}
		return "";
	}

	public static String toDateTimeString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (date != null) {
			return sdf.format(date);
		}
		return "";

	}

	public static String toDateTimeStringWithoutSeconds(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		if (date != null) {
			return sdf.format(date);
		}
		return "";

	}

	public static String toDateString(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (timestamp != null) {
			return sdf.format(timestamp);
		}
		return "";

	}
	
	public static String toDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (date != null) {
			return sdf.format(date);
		}
		return "";

	}

	public static String toDateStringDb(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null) {
			return sdf.format(date);
		}
		return "";

	}
	
	public static Date toDate(String date, String format, Logger logger) {
		if (date != null && date.length() > 0 && format != null && format.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				return sdf.parse(date);
			} catch (Exception e) {
				logger.error("Erro na conversao de datas \"toDate\" ==> ",e);
			}
		}
		return null;
	}

	public static String toString(Date date, String format, Logger logger) {
		if (date != null && format != null && format.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				return sdf.format(date);
			} catch (Exception e) {
				logger.error("Erro na conversao de datas \"toString\" ==> ",e);
			}
		}
		return null;
	}
	
	public static String toDateString(Timestamp timestamp, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (timestamp != null) {
			return sdf.format(timestamp);
		}
		return "";
	}

	public static long vencDate(Date dt) {
		Date dtAtual = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		Date dtVenci = DateUtils.truncate(dt, Calendar.DAY_OF_MONTH);

		long differenceMilliSeconds = dtVenci.getTime() - dtAtual.getTime();

		return (differenceMilliSeconds / 1000 / 60 / 60 / 24);
	}
	
  
    public static Date addDaysSelect(String data, int numDays){
    	Calendar calendar  = new GregorianCalendar();
    	String[] splitData = data.split("/");
    	calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1])-1, Integer.parseInt(splitData[0]));
		calendar.add(Calendar.DAY_OF_MONTH, numDays);
    	Date newDate = new Date(calendar.getTimeInMillis());
    	return newDate;
    }
    
    /**
     * Adicionando 00:00:00 a data informada
     * @param data
     * @return date object
     */
    public static Date addMinHourToDate(String data){
    	Calendar calendar  = new GregorianCalendar();
    	String[] splitData = data.split("/");
    	calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1])-1, Integer.parseInt(splitData[0]), 00, 00, 00);
    	return calendar.getTime();
    }
    
    /**
     * Adicionando 23:59:59 a data informada
     * @param data
     * @return date object
     */
    public static Date addMaxHourToDate(String data){
    	Calendar calendar  = new GregorianCalendar();
    	String[] splitData = data.split("/");
    	calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1])-1, Integer.parseInt(splitData[0]), 23, 59, 59);
    	return calendar.getTime();
    }
    
    public static boolean validaPeriodoDataInicialMaiorDataFinal(Date dataInicial, Date dataFinal){
		return dataInicial.after(dataFinal);
    }
    
    public static Date toDateFromXmlDate(String xmlDate){
    	return DatatypeConverter.parseDate(xmlDate).getTime();
    }
    
 
}
