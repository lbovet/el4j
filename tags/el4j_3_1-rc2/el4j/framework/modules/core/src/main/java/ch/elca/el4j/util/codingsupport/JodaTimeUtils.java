/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.util.codingsupport;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 
 * This class is a class providing static mehtods for conversion between JodaTime formats and strings.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH)
 */
public class JodaTimeUtils {
	
	
	/**
	 * dd.MM.YYYY
	 */
	public static final String DEFAULT_DATE_WITH_PERIOD = "dd.MM.YYYY";
	
	/**
	 *  dd:MM:YYYY 
	 */
	public static final String DEFAULT_DATE_WITH_COLON = "dd:MM:YYYY";
	
	/**
	 * dd MM YYYY
	 */
	public static final String DEFAULT_DATE_WITH_WHITESPACE = "dd MM YYYY";
	
	
	/**
	 * HH.mm.ss 
	 */
	public static final String TIME_WITH_PERIOD = "HH.mm.ss"; 
	
	/**
	 * HH:mm:ss
	 */
	public static final String TIME_WITH_COLON = "HH:mm:ss";
	
	/**
	 * HH mm ss
	 */
	public static final String TIME_WITH_WHITESPACE = "HH mm ss";
	
	/**
	 * dd.MM.YY HH:mm:ss
	 */
	public static final String DEFAULT_DATETIME = DEFAULT_DATE_WITH_PERIOD + " " + TIME_WITH_COLON;
	
	
	
	/**
	 * Hide default constructor.
	 */
	protected JodaTimeUtils() {}	
	
	/**
	 * Returns a string containing the date and/or time from the DateTime.
	 * The format of the returned string is defined by the pattern.
	 * (e.g. "dd.MM.YYYY hh.mm.ss" for "26.8.2009 08.26.52") 
	 * 
	 * @param date is the DateTime to process
	 * @param pattern is the pattern to apply
	 * @return a string with the date/time in the specified format
	 */
	public static String getDateTimeString(DateTime date, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return fmt.print(date);
	}
	
	/**
	 * Returns a string containing the date in a LocalDate.
	 * The format of the returned string is defined by the pattern.
	 * Attention: any time related characters in the pattern will be 
	 * set to 0. 
	 * (e.g. 'mm' in the pattern will result in a '00' as there is no time in a LocalDate.
	 * 
	 * @param date is the LocalDate to process
	 * @param pattern is the pattern to apply
	 * @return a string with the date in the format specified by pattern
	 */	
	public static String getLocalDateString(LocalDate date, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return fmt.print(date);
	}
	
	/**
	 * Returns a string containing the time in a LocalTime.
	 * The format of the returned string is defined by the pattern.
	 * Attention: any date related characters will be set to 0.
	 * (e.g. 'MM' in the pattern will result in a '00').
	 * 
	 * @param time is the LocalTime to process
	 * @param pattern is the pattern to apply
	 * @return a string with the time in the format specified by pattern
	 */
	public static String getLocalTimeString(LocalTime time, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return fmt.print(time);
	}
	
	/**
	 * Returns a DateTime containing the date/time in the string.
	 * The dateTime string must match the pattern so that all values can be set. 
	 * 
	 * @param dateTime is the string to process
	 * @param pattern is the pattern to apply
	 * @return a DateTime object that contains all values that could be matched
	 */
	public static DateTime parseDateTime(String dateTime, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return fmt.parseDateTime(dateTime);	
	}
	
	/**
	 * Returns a LocalDate containing the date in the string.
	 * The date string must match the pattern so that all values can be set. 
	 * 
	 * @param date is the string to process
	 * @param pattern is the pattern to apply
	 * @return a LocalDate object that contains all values that could be matched
	 */
	public static LocalDate parseLocalDate(String date, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return fmt.parseDateTime(date).toLocalDate();
		
	}
	
	/**
	 * Returns a LocalTime containing the time in the string.
	 * The date string must match the pattern so that all values can be set. 
	 * 
	 * @param time is the string to process
	 * @param pattern is the pattern to apply
	 * @return a LocalTime object that contains all values that could be matched
	 */
	public static LocalTime parseLocalTime(String time, String pattern) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
		return fmt.parseDateTime(time).toLocalTime();	
	}
	
	/**
	 * Returns a localized DateTime containing the time in the string.
	 * The dateTime string must match the pattern so that all values can be set. 
	 * 
	 * @param dateTime is the string to process
	 * @param locale is the local which is applied to the returned DateTime object
	 * @param pattern is the pattern to apply
	 * @return a DateTime object that contains all values that could be matched
	 */
	public static DateTime parseDateTime(String dateTime, String pattern, Locale locale) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern).withLocale(locale);
		return fmt.parseDateTime(dateTime);
	}
	
	
	/**
	 * Creates a DateTime out of a LocalDate and a LocalTime.
	 * 
	 * @param date is the LocalDate containing the desired date
	 * @param time is the LocalTime containing the desired time
	 * @return a new DateTime which is created out of the two arguments
	 */
	
	public static DateTime mergeLocalDateAndLocalTime(LocalDate date, LocalTime time) {
		return date.toDateTime(time);
	}
	
	/**
	 * Changes the time (but not the date) of a DateTime to the time in a LocalTime.
	 * 
	 * @param dateTime is the DateTime to set
	 * @param time is the time time to set to
	 * @return a DateTime object with the date of dateTime and the time of time
	 */
	
	public static DateTime setTimeOfDateTime(DateTime dateTime, LocalTime time) {
		return dateTime.millisOfDay().setCopy(time.getMillisOfDay());
	}
	
	/**
	 * Sets the Date (but not the time) of a DateTime to the value in date.
	 * 
	 * @param dateTime is the DateTime for which we set the date
	 * @param date is the LocalDate to set the date to
	 * @return a DateTime object with the date of date and the time of dateTime
	 */
	
	public static DateTime setDateOfDateTime(DateTime dateTime, LocalDate date) {
		LocalTime time = dateTime.toLocalTime();
		DateTime newDateTime = new DateTime(date);
		newDateTime = newDateTime.millisOfDay().setCopy(time.getMillisOfDay());
		return newDateTime.withChronology(dateTime.getChronology());
	
	}		
}
