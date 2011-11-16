package no.kommune.bergen.soa.common.calendar;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * For å holde oversikt over arbeidsdager og fridager.
 *
 * @author einarvalen@gmail.com
 */
public class BusinessCalendar {
	private final Set<String> leave = new HashSet<String>();
	private final Date fromInclusive, untilInclusive;

	public BusinessCalendar( Date fromInclusive, Date untilInclusive, Set<Date> vacationDays ) {
		this.fromInclusive = fromInclusive;
		this.untilInclusive = untilInclusive;
		for (Date date : vacationDays) {
			if (!isWithinBounds( date )) throw new IllegalArgumentException( msg( "One or more vacation days are outside stated calender limits", date ) );
			this.leave.add( CalendarHelper.formatAsDate( date ) );
		}
	}

	private String msg( String msg, Date date ) {
		String from = CalendarHelper.formatAsDateAndTime( fromInclusive );
		String until = CalendarHelper.formatAsDateAndTime( untilInclusive );
		String problem = CalendarHelper.formatAsDateAndTime( date );
		return String.format( "%s. (fromInclusive=%s, untilInclusive=%s, problemDate=%s)", msg, from, until, problem );
	}

	private boolean isWithinBounds( Date date ) {
		return CalendarHelper.isWithinBounds( this.fromInclusive, this.untilInclusive, date );
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
		if (!isWithinBounds( date )) throw new IllegalArgumentException( msg( "Date is outside stated calender limits", date ) );
		return leave.contains( CalendarHelper.formatAsDate( date ) );
	}

}
