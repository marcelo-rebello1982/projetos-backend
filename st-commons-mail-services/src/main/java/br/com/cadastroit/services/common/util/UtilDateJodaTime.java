package br.com.cadastroit.services.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.cadastroit.services.enums.TimeZoneEnum;

public class UtilDateJodaTime {

    private static DateTime toDateTime(String timeZone, DateTime... dt){
        DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        DateTime dateTime = null;
        if(dt.length > 0){
            dt[0] = new DateTime(dateTimeZone);
            dateTime = dt[0];
        }else{
            dateTime = new DateTime(dateTimeZone);
        }
        return dateTime;
    }
    private static DateTimeFormatter toDateTimeFormatter(String format){
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        return formatter;
    }

    public static Date addDays(int days){
        DateTime date = new DateTime();
        date = date.plusDays(days);
        return date.toDate();
    }
    public static Date toDateTimeZone(String timeZone){
        return toDateTime(timeZone).toDate();
    }
    public static DateTime toDateTimeZoneUTC(String timeZone){
        return toDateTime(timeZone).toDateTime();
    }
    public static long toDateTimeMillis(String timeZone){
        return toDateTime(timeZone).toDateTime().getMillis();
    }
    public static String toDateString(String format){
        DateTime dt = new DateTime();
        return dt.toString(format);
    }
    public static Date toDate(String format, String date){
        DateTime dateTime = toDateTimeFormatter(format).parseDateTime(date);
        return dateTime.toDate();
    }
    public static Date toDate(java.util.Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime.toDate();
    }
    public static DateTime toDateTime(java.util.Date date){
        DateTime dateTime = new DateTime(date);
        return dateTime;
    }
    public static DateTime plusDaysToDate(java.util.Date date, int days){
        DateTime dateTime = new DateTime(date.getTime());
        dateTime.plusDays(days);
        return dateTime;
    }

    public static Date toDate(Long dateMillis){
        DateTime dateTime = new DateTime(dateMillis.longValue());
        return dateTime.toDate();
    }
    public static DateTime toDateUTC(java.util.Date date, String timeZone){
        DateTime dateTime = new DateTime(date,DateTimeZone.forID(timeZone));
        return dateTime.toDateTime();
    }
    public static DateTime toDateUTC(Long dateMillis, String timeZone){
        DateTime dateTime = new DateTime(dateMillis.longValue(),DateTimeZone.forID(timeZone));
        return dateTime.toDateTime();
    }
    public static long toTimeMillis(String format, String date){
        DateTime dateTime = toDateTimeFormatter(format).parseDateTime(date);
        return dateTime.getMillis();
    }
    public static Date toDateTimeZone(String format, String timeZone, String date){
        DateTime dateTime = toDateTimeFormatter(format).parseDateTime(date);
        return toDateTime(timeZone,dateTime).toDate();
    }
    public static DateTime toDateTimeZoneUTC(String format, String timeZone, String date){
        DateTime dateTime = toDateTimeFormatter(format).parseDateTime(date);
        return toDateTime(timeZone,dateTime);
    }
    public static Date toDate(){
        DateTime dt = new DateTime();
        return dt.toDate();
    }
    public static String formatDateTimeSefaz(Date date, String timeZone){
        String dataPura 	 = UtilDateJodaTime.toDateUTC(date, timeZone).toString();
        String dataFormatada = dataPura.substring(0, 19)+dataPura.substring(23, 29);
        return dataFormatada;
    }
    public static Date addTimeDate(java.util.Date date, String hour, String minute, String second){
        DateTime dateTime = new DateTime(date);
        dateTime.plusHours(Integer.parseInt(hour));
        dateTime.plusMinutes(Integer.parseInt(minute));
        dateTime.plusSeconds(Integer.parseInt(second));

        return dateTime.toDate();
    }
    public static Date newDateTimeZone(String format, String timeZone, Date data){
        SimpleDateFormat formata = new SimpleDateFormat(format);
        String dateString = toDateTimeZoneUTC(format, timeZone, formata.format(data)).toString();
        String[] dateTime = dateString.split("T");
        String[] d = dateTime[0].split("-");
        String timeString = dateTime[1].split("\\.")[0];
        String[] t = timeString.split(":");
        Calendar c = new GregorianCalendar();
        c.set(Integer.parseInt(d[0]), Integer.parseInt(d[1])-1, Integer.parseInt(d[2]),
                Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
        Date date = c.getTime();
        return date;
    }
    public static Date newDateTimeZoneAddDays(String format, String timeZone, Date data, int numDays){
        SimpleDateFormat formata = new SimpleDateFormat(format);
        String dateString = toDateTimeZoneUTC(format, timeZone, formata.format(data)).toString();
        String[] dateTime = dateString.split("T");
        String[] d = dateTime[0].split("-");
        String timeString = dateTime[1].split("\\.")[0];
        String[] t = timeString.split(":");
        Calendar c = new GregorianCalendar();
        c.set(Integer.parseInt(d[0]), Integer.parseInt(d[1])-1, Integer.parseInt(d[2]),
                Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
        c.add(Calendar.DAY_OF_MONTH, numDays);
        Date date = c.getTime();
        return date;
    }
    public static void main(String[] args){
		/*System.out.println(toDateTimeZone(TimeZoneEnum.America_Sao_Paulo.toString()));
		System.out.println(toDateString("yyyy-MM-dd'T'HH:mm:ssZ"));
		System.out.println(toDateString("yyyy-MM-dd'T'HH:mm:ss"));
		System.out.println("XML Greg Calendar ==> "+UtilDate.toXMLGregorianCalendarNow());
		System.out.println(toDate("yyyy-MM-dd'T'HH:mm:ss",toDateString("yyyy-MM-dd'T'HH:mm:ss")));
		System.out.println(toDate());
		System.out.println(toDate("yyyy-MM-dd'T'HH:mm:ssZ", "2014-07-31T09:59:52-03:00"));
		System.out.println(toDate("yyyy-MM-dd", "2014-07-31"));
		System.out.println(toDate("dd-MM-yyyy", "31-07-2014"));
		System.out.println(toTimeMillis("dd-MM-yyyy", "31-07-2014"));
		System.out.println(toDateTimeZone("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(),"31-07-2014"));
		System.out.println(toDateTimeZoneUTC("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(),"31-07-2014"));
		System.out.println(toDate(new Date()));
		System.out.println(toDateUTC(new Date(), TimeZoneEnum.America_Sao_Paulo.toString()));
		System.out.println(toDate(new Date().getTime()));
		System.out.println(toDateUTC(new Date().getTime(), TimeZoneEnum.America_Sao_Paulo.toString()));
		System.out.println(addDays(-10));
		System.out.println(formatDateTimeSefaz(new Date(), TimeZoneEnum.America_Sao_Paulo.toString()));

		System.out.println(getMonthName(12, 2017));
		System.out.println(getDaysFromMonth(12, 2017));
		getDaysOfShortWeek(11, 2017, 10);
		for(int i = 11; i < 18; i++){
			System.out.println(getDaysOcsvWritereek(12, 2017, i));
		}*/
		/*System.out.println(toDate());
		String d = toDateTimeZoneUTC("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(),"25-10-2017").toString();
		String[] splitD = d.split("T");
		String date = splitD[0];
		String time = splitD[1].split("\\.")[0];

		System.out.println(date+" "+time);
		Calendar c = new GregorianCalendar();
		c.set(Integer.parseInt(date.split("-")[0]),
		      Integer.parseInt(date.split("-")[1])-1,
		      Integer.parseInt(date.split("-")[2]),
		      Integer.parseInt(time.split(":")[0]),
		      Integer.parseInt(time.split(":")[1]),
		      Integer.parseInt(time.split(":")[2]));
		Date data = c.getTime();
		System.out.println(data);
		System.out.println(UtilDate.toDateString(new Timestamp(data.getTime()), "dd/MM/yyyy HH:mm:ss"));*/
        System.out.println(newDateTimeZone("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(), new Date()));
        System.out.println(newDateTimeZone("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(), new Date()).getTime());
        Long timeAgedamento = newDateTimeZone("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(), new Date()).getTime();
        System.out.println(new Date().toString());
        System.out.println(new Date().getTime());
        Long timeNow = new Date().getTime();
        System.out.println("timeNow >= timeAgendamento [" + (timeNow <=  timeAgedamento) + "]");

        Date d = newDateTimeZone("dd-MM-yyyy", TimeZoneEnum.America_Sao_Paulo.toString(), new Date());
        System.out.println(new java.sql.Date(UtilDate.addMaxHourToDate(UtilDate.toDateString(d)).getTime()));

		/*Date d = toDate("dd/MM/yyyy HH:mm:ss", "01/01/2017 06:30:25");
		System.out.println(d);
		System.out.println(UtilDate.toDateTimeString(toDate(System.currentTimeMillis())));
		System.out.println(UtilDate.toDateTimeString(toDate(System.currentTimeMillis())));*/
    }
}

