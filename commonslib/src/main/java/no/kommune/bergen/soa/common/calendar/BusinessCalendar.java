package no.kommune.bergen.soa.common.calendar;

import java.util.*;

/**
 * For å holde oversikt over arbeidsdager og fridager.
 *
 * @author einarvalen@gmail.com
 */
public class BusinessCalendar {
	private static final HashMap<Integer,Set<Date>> leave = new HashMap<Integer, Set<Date>>();

	public BusinessCalendar() {
	}

	private boolean isWithinBounds( Date date ) {
		return true;
	}

	public Date nextWorkday( Date date ) {
		Date d = CalendarHelper.addDays( date, 1 );
		while (isDayOff( d )) {
			d = CalendarHelper.addDays( d, 1 );
		}
		return d;
	}

	public Date previousWorkday( Date date ) {
		Date d = CalendarHelper.addDays( date, -1 );
		while (isDayOff( d )) {
			d = CalendarHelper.addDays( d, -1 );
		}
		return d;
	}

	public Date nextDayOff( Date date ) {
		Date d = CalendarHelper.addDays( date, 1 );
		while (isWorkday( d )) {
			d = CalendarHelper.addDays( d, 1 );
		}
		return d;
	}

	public Date previousDayOff( Date date ) {
		Date d = CalendarHelper.addDays( date, -1 );
		while (isWorkday( d )) {
			d = CalendarHelper.addDays( d, -1 );
		}
		return d;
	}

	public boolean isWorkday( Date date ) {
		return !isDayOff( date );
	}

	public boolean isDayOff( Date date ) {
		int year = CalendarHelper.newCalendar(date).get(Calendar.YEAR);
		Set<Date> dates = leave.get(year);
		if(dates == null) {
			dates =new NorwegianBankHolidays(year);
			leave.put(year, dates );
		}
		return dates.contains( CalendarHelper.clearTimePart(date) );
	}

}
