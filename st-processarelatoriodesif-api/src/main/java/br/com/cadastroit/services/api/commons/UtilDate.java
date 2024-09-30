package br.com.cadastroit.services.api.commons;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtilDate {

	/**
     * Adicionando 00:00:00 a data informada
     * @param data
     * @return date object
     */
    public static java.util.Date addMinHourToDate(String data){
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
    public static java.util.Date addMaxHourToDate(String data){
    	Calendar calendar  = new GregorianCalendar();
    	String[] splitData = data.split("/");
    	calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1])-1, Integer.parseInt(splitData[0]), 23, 59, 59);
    	return calendar.getTime();
    }
    
    public static java.util.Date addDaysSelect(String data, int numDays){
    	Calendar calendar  = new GregorianCalendar();
    	String[] splitData = data.split("/");
    	calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1])-1, Integer.parseInt(splitData[0]));
		calendar.add(Calendar.DAY_OF_MONTH, numDays);
    	java.util.Date newDate = new java.util.Date(calendar.getTimeInMillis());
    	return newDate;
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
    
    private static DateTimeFormatter toDateTimeFormatter(String format){
		DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
		return formatter;
	}
    
    public static Date toDate(String format, String date){
		DateTime dateTime = toDateTimeFormatter(format).parseDateTime(date);
		return dateTime.toDate();
	}
    
    public static String toString(Date date, String format) {
		if (date != null && format != null && format.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				return sdf.format(date);
			} catch (Exception e) {
				log.error("Erro na conversao de datas \"toString\" ==> ",e);
			}
		}
		return null;
	}
	
}
