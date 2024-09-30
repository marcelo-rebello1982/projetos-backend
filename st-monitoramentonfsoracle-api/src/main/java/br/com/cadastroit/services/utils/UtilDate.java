package br.com.cadastroit.services.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class UtilDate {

    private static DateTime toDateTime(String timeZone, DateTime... dt) {
        DateTimeZone dateTimeZone = DateTimeZone.forID(timeZone);
        DateTime dateTime = null;
        if (dt.length > 0) {
            dt[0] = new DateTime(dateTimeZone);
            dateTime = dt[0];
        } else {
            dateTime = new DateTime(dateTimeZone);
        }
        return dateTime;
    }

    public static Date addDays(int days) {
        DateTime date = new DateTime();
        date = date.plusDays(days);
        return date.toDate();
    }

    public static String toDateString(String format) {
        DateTime dt = new DateTime();
        return dt.toString(format);
    }

    public static Date toDate(java.util.Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime.toDate();
    }

    public static DateTime toDateTime(java.util.Date date) {
        DateTime dateTime = new DateTime(date);
        return dateTime;
    }

    public static DateTime plusDaysToDate(java.util.Date date, int days) {
        DateTime dateTime = new DateTime(date.getTime());
        dateTime.plusDays(days);
        return dateTime;
    }

    public static Date toDate(Long dateMillis) {
        DateTime dateTime = new DateTime(dateMillis.longValue());
        return dateTime.toDate();
    }

    public static DateTime toDateUTC(java.util.Date date, String timeZone) {
        DateTime dateTime = new DateTime(date, DateTimeZone.forID(timeZone));
        return dateTime.toDateTime();
    }

    public static DateTime toDateUTC(Long dateMillis, String timeZone) {
        DateTime dateTime = new DateTime(dateMillis.longValue(), DateTimeZone.forID(timeZone));
        return dateTime.toDateTime();
    }

    public static Date toDate() {
        DateTime dt = new DateTime();
        return dt.toDate();
    }

    public static Date addTimeDate(java.util.Date date, String hour, String minute, String second) {
        DateTime dateTime = new DateTime(date);
        dateTime.plusHours(Integer.parseInt(hour));
        dateTime.plusMinutes(Integer.parseInt(minute));
        dateTime.plusSeconds(Integer.parseInt(second));

        return dateTime.toDate();
    }

    public String toString(Date date, String format) {
        if (date != null && format != null && format.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                return sdf.format(date);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static String toDateString(Date date) {
        return date != null ? new SimpleDateFormat("dd/MM/yyyy").format(date) : "";
    }

    public static Date addMinHourToDate(String data) {
        Calendar calendar = new GregorianCalendar();
        String[] splitData = data.split("/");
        calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]) - 1, Integer.parseInt(splitData[0]),
                00, 00, 00);
        return calendar.getTime();
    }

    public static Date addMaxHourToDate(String data) {
        Calendar calendar = new GregorianCalendar();
        String[] splitData = data.split("/");
        calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]) - 1, Integer.parseInt(splitData[0]),
                23, 59, 59);
        return calendar.getTime();
    }


    public static String toDateString_(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (timestamp != null) {
            return sdf.format(timestamp);
        }
        return "";

    }

    public static String toDateString__(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (date != null) {
            return sdf.format(date);
        }
        return "";
    }

    public static Timestamp convertStringDateToTimestamp(String dateString, String pattern) {
        LocalDateTime dateTime = LocalDateTime.parse(dateString,
                DateTimeFormatter.ofPattern(pattern));
        return Timestamp.valueOf(dateTime);
    }

    public static Timestamp convertStringDateToTimestamp_(String dateString) {
        return Timestamp.valueOf(LocalDateTime.parse(dateString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
    }

    public static java.util.Date addDaysSelect(String data, int numDays) {
        Calendar calendar = new GregorianCalendar();
        String[] splitData = data.split("/");
        calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]) - 1,
                Integer.parseInt(splitData[0]));
        calendar.add(Calendar.DAY_OF_MONTH, numDays);
        java.util.Date newDate = new java.util.Date(calendar.getTimeInMillis());
        return newDate;
    }

    public static Date actualDateLessDay(Integer subtractDays) {
        LocalDateTime dateLastMonth = LocalDateTime.now().minusDays(subtractDays);
        return Date.from(dateLastMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static java.util.Date getParsedDate(String value) throws Exception {
        String[] formats = new String[]{"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"};
        for (String format : formats) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("pt", "BR"));
                String date = value.toString();
                java.util.Date data = (!date.equals("")) ? formatter.parse(date) : null;
                return data;
            } catch (ParseException e) {
            }
        }
        throw new Exception("Erro de convers?o Date [" + value + "].");
    }

    public static Timestamp getParsedTimestamp(String value) throws Exception {
        String[] formats = new String[]{"yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd"};
        for (String format : formats) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("pt", "BR"));
                String date = value.toString();
                java.util.Date parsedDate = (!date.equals("")) ? formatter.parse(date) : null;
                java.sql.Timestamp data = (parsedDate != null) ? new java.sql.Timestamp(parsedDate.getTime()) : null;
                return data;
            } catch (ParseException e) {
            }
        }
        throw new Exception("Erro de convers?o Timestamp [" + value + "].");
    }

    public static String getDateTime(Long timeAdd) {
        Date d = new Date();
        Long time = d.getTime();
        Long newTime = time + timeAdd;

        String date = toDateTimeString(new Timestamp(newTime));
        return date;
    }

    public static String getDateTimeNow() {
        return UtilDate.toDateTimeString(new Timestamp(new Date().getTime()));
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
        return date.substring(0, 22) + ":" + date.substring(22, 24);
    }

    public static String toDateTimeStringWithoutSeconds(Timestamp timestamp) {
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

    public static String toDateStringDb(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (date != null) {
            return sdf.format(date);
        }
        return "";

    }

    public static Date toDate_(String date, String format) {
        if (date != null && date.length() > 0 && format != null && format.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                return sdf.parse(date);
            } catch (ParseException e) {
            }
        }
        return null;
    }


    public static String toDateString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (date != null) {
            return sdf.format(date);
        }
        return "";
    }

    public static String toDateString(Timestamp timestamp, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (timestamp != null) {
            return sdf.format(timestamp);
        }
        return "";
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(java.util.Date date) throws Exception {
        if (date == null) {
            return null;
        } else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        }
    }

    public static XMLGregorianCalendar toXMLGregorianCalendarDate(java.util.Date date) throws Exception {
        if (date == null) {
            return null;
        } else {
            GregorianCalendar gc = new GregorianCalendar();
            String d = toDateTimeString(new Timestamp(date.getTime()));
            Integer dia = Integer.parseInt(d.split("/")[0]);
            Integer mes = Integer.parseInt(d.split("/")[1]);
            Integer ano = Integer.parseInt(d.split("/")[2].split(" ")[0]);

            gc.set(ano, (mes - 1), dia);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        }
    }

    public static XMLGregorianCalendar toXMLGregorianCalendarNow(Date d) {
        XMLGregorianCalendar xmlDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String date = sdf.format(d);

            d = sdf.parse(date);
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(d);

            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc.get(Calendar.YEAR),
                    gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), gc.get(Calendar.HOUR_OF_DAY),
                    gc.get(Calendar.MINUTE), gc.get(Calendar.SECOND), DatatypeConstants.FIELD_UNDEFINED,
                    DatatypeConstants.FIELD_UNDEFINED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlDate;
    }

    public static XMLGregorianCalendar toXMLGregorianCalendarGMT(java.util.Date date, TimeZone timeZone)
            throws Exception {
        if (date == null) {
            return null;
        } else {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            gc.setTimeZone(timeZone);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
        }
    }

    public static java.util.Date toDate(XMLGregorianCalendar xgc) {
        if (xgc == null) {
            return null;
        } else {
            return xgc.toGregorianCalendar().getTime();
        }
    }

    public static boolean validaPeriodoDataInicialMaiorDataFinal(Date dataInicial, Date dataFinal) {
        return dataInicial.after(dataFinal);
    }

    public static java.util.Date toDateFromXmlDate(String xmlDate) {
        return DatatypeConverter.parseDate(xmlDate).getTime();
    }

    public static void main(String[] args) {
        try {
            // System.out.println(UtilDate.toXMLGregorianCalendar(UtilDateJodaTime.toDateUTC(new
            // Date(), TimeZoneEnum.America_Sao_Paulo.toString())));
            String date = "2016-07-19 00:00:00.0";
            Date toDateTimeUTC = UtilDate.getParsedDate(date.replace("00.0", "00"));
            //	String toDateString = UtilDate.toDateString(toDateTimeUTC);
            String toDateTimeWithoutSeconds = UtilDate
                    .toDateTimeStringWithoutSeconds(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
            String timeNow = UtilDate.toDateTimeStringTextPlain(new Timestamp(System.currentTimeMillis()));

            String[] splitTime = toDateTimeWithoutSeconds.split(" ");
            Integer hora = Integer.parseInt(splitTime[1].split(":")[0]);
            //	System.out.println(toDateString);
            System.out.println(timeNow);
            System.out.println(toDateTimeWithoutSeconds + ", HORA = " + hora);

            //String dateNow = toString(new Date(System.currentTimeMillis()), "dd/MM/yyyy HH:mm:ss");
            //System.out.println("DATE = " + dateNow.split(" ")[0]);
            //System.out.println("TIME = " + dateNow.split(" ")[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
