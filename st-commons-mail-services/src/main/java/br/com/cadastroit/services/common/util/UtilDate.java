package br.com.cadastroit.services.common.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

public class UtilDate {

	private static Logger logger = Logger.getLogger(UtilDate.class);

	public static java.util.Date getParsedDate(String value) throws Exception {

		String[] formats = new String[] { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
		for (String format : formats) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("pt", "BR"));
				String date = value.toString();
				java.util.Date data = (!date.equals("")) ? formatter.parse(date) : null;
				return data;
			} catch (ParseException e) {
			}
		}
		throw new Exception("Erro de conversao Date [" + value + "].");
	}

	public static Timestamp getParsedTimestamp(String value) throws Exception {

		String[] formats = new String[] { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
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
		throw new Exception("Erro de conversao Timestamp [" + value + "].");
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

	public static Date toDate(String date, String format) {

		if (date != null && date.length() > 0 && format != null && format.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				return sdf.parse(date);
			} catch (Exception e) {
				logger.error("Erro na conversao de datas \"toDate\" ==> ", e);
			}
		}
		return null;
	}

	public static String toString(Date date, String format) {

		if (date != null && format != null && format.length() > 0) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				return sdf.format(date);
			} catch (Exception e) {
				logger.error("Erro na conversao de datas \"toString\" ==> ", e);
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

	public static Calendar toXMLCalendarNowWithTime(Date d) {

		Calendar c = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date dateTime = sdfTime.parse(UtilDateJodaTime.toDateString("yyyy-MM-dd'T'HH:mm:ss"));
			String date = sdf.format(d);
			d = sdf.parse(date);

			GregorianCalendar gc = new GregorianCalendar();
			GregorianCalendar gc2 = new GregorianCalendar();
			gc.setTime(d);
			gc2.setTime(dateTime);

			c = new GregorianCalendar();
			c.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), gc2.get(Calendar.HOUR_OF_DAY),
					gc2.get(Calendar.MINUTE), gc2.get(Calendar.SECOND));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	public static java.util.Date addDaysSelect(String data, int numDays) {

		Calendar calendar = new GregorianCalendar();
		String[] splitData = data.split("/");
		calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]) - 1, Integer.parseInt(splitData[0]));
		calendar.add(Calendar.DAY_OF_MONTH, numDays);
		java.util.Date newDate = new java.util.Date(calendar.getTimeInMillis());
		return newDate;
	}

	/**
	 * Adicionando 00:00:00 a data informada
	 * 
	 * @param data
	 * @return date object
	 */
	public static java.util.Date addMinHourToDate(String data) {

		Calendar calendar = new GregorianCalendar();
		String[] splitData = data.split("/");
		calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]) - 1, Integer.parseInt(splitData[0]), 00, 00, 00);
		return calendar.getTime();
	}

	/**
	 * Adicionando 23:59:59 a data informada
	 * 
	 * @param data
	 * @return date object
	 */
	public static java.util.Date addMaxHourToDate(String data) {

		Calendar calendar = new GregorianCalendar();
		String[] splitData = data.split("/");
		calendar.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]) - 1, Integer.parseInt(splitData[0]), 23, 59, 59);
		return calendar.getTime();
	}

	public static boolean validaPeriodoDataInicialMaiorDataFinal(Date dataInicial, Date dataFinal) {

		return dataInicial.after(dataFinal);
	}

	public static java.util.Date toDateFromXmlDate(String xmlDate) {

		return DatatypeConverter.parseDate(xmlDate).getTime();
	}

	public static void main(String[] args) {

		try {
			// System.out.println(toXMLGregorianCalendar(UtilDateJodaTime.toDateUTC(new Date(),
			// TimeZoneEnum.America_Sao_Paulo.toString())));
			String date = "2016-07-19 00:00:00.0";
			Date toDateTimeUTC = UtilDate.getParsedDate(date.replace("00.0", "00"));
			String toDateString = UtilDate.toDateString(toDateTimeUTC);
			String toDateTimeWithoutSeconds = UtilDate.toDateTimeStringWithoutSeconds(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
			String timeNow = UtilDate.toDateTimeStringTextPlain(new Timestamp(System.currentTimeMillis()));

			String[] splitTime = toDateTimeWithoutSeconds.split(" ");
			Integer hora = Integer.parseInt(splitTime[1].split(":")[0]);
			System.out.println(toDateString);
			System.out.println(timeNow);
			System.out.println(toDateTimeWithoutSeconds + ", HORA = " + hora);

			String dateNow = UtilDate.toString(new Date(System.currentTimeMillis()), "dd/MM/yyyy HH:mm:ss");
			System.out.println("DATE = " + dateNow.split(" ")[0]);
			System.out.println("TIME = " + dateNow.split(" ")[1]);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
