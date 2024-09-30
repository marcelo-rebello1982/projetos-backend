package br.com.cadastroit.services.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CommonsNumberFormat {

	public String formatterResultNumber(Double value, String mask) {
		double inSeconds= value/1000;
		NumberFormat formatter = new DecimalFormat(mask,DecimalFormatSymbols.getInstance(new Locale("en", "US")));
		String valueProcess = formatter.format(inSeconds);
		return valueProcess;
	}
	
}
