package br.com.complianceit.enterprise.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class UtilNumber {

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
	        } catch (java.lang.IllegalArgumentException e) {
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
}
