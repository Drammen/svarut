package no.kommune.bergen.soa.common.calendar;

import java.util.Date;
import java.util.Set;

/** @author EinarValen@gmail.com */
public class PresentDayBusinessCalendarForNorway {

	private PresentDayBusinessCalendarForNorway() {}

	public static BusinessCalendar getInstance() {
		int year = CalendarHelper.extractYear( new Date() );
		Set<Date> vacationDays = new NorwegianBankHolidays( year );
		vacationDays.addAll( new NorwegianBankHolidays( year + 1 ) );
		Date from = CalendarHelper.toDate( year, 1, 1 );
		Date to = CalendarHelper.toDate( year + 1, 12, 31 );
		return new BusinessCalendar( from, to, vacationDays );
	}

}
