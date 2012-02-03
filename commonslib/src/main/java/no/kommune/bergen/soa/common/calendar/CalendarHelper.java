package no.kommune.bergen.soa.common.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/** @author einarvalen@gmail.com */
public class CalendarHelper {

	public static Date clearTimePart( Date date ) {
		Calendar calendar = newCalendar( date );
		int y = calendar.get( Calendar.YEAR );
		int m = calendar.get( Calendar.MONTH );
		int d = calendar.get( Calendar.DAY_OF_MONTH );
		calendar.clear();
		calendar.set( y, m, d );
		return calendar.getTime();
	}

	public static long extractTimePart( Date date ) {
		Calendar calendar = newCalendar( date );
		calendar.set( 1970, 0, 1 );
		return calendar.getTimeInMillis();
	}

	public static int extractYear( Date date ) {
		return newCalendar( date ).get( Calendar.YEAR );
	}

	public static Calendar newCalendar( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		return calendar;
	}

	public static int extractDayOfMonth( Date date ) {
		return newCalendar( date ).get( Calendar.DAY_OF_MONTH );
	}

	public static int extractMonth( Date date ) {
		return newCalendar( date ).get( Calendar.MONTH ) + 1;
	}

	public static Date toDate( int year, int month, int day ) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set( year, month - 1, day, 0, 0, 0 );
		return calendar.getTime();
	}

	public static String formatAsDateAndTime( Date date ) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		return dateFormat.format( date );
	}

	public static String formatAsDate( Date date ) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		return dateFormat.format( date );
	}

	public static String formatAsTime( Date date ) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm:ss" );
		return dateFormat.format( date );
	}

	public static Date addDays( Date date, int days ) {
		Calendar calendar = newCalendar( date );
		calendar.add( Calendar.DAY_OF_YEAR, days );
		return calendar.getTime();
	}

	public static Set<Date> weekends( int year ) {
		Set<Date> dates = new HashSet<Date>();
		Calendar calendar = newCalendar( toDate( year, 1, 1 ) );
		while (true) {
			if (year != calendar.get( Calendar.YEAR )) break;
			int dayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );
			if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
				dates.add( calendar.getTime() );
			}
			calendar.add( Calendar.DAY_OF_YEAR, 1 );
		}
		return dates;
	}

	public static boolean isWithinBounds( Date fromInclusive, Date toInclusive, Date dateToTest ) {
		Calendar after = newCalendar( fromInclusive );
		after.add( Calendar.MILLISECOND, -1 );
		Calendar cal = newCalendar( dateToTest );
		Calendar before = newCalendar( toInclusive );
		before.add( Calendar.MILLISECOND, 1 );
		if (cal.after( after )) {
			return cal.before( before );
		}
		return false;
	}

}
