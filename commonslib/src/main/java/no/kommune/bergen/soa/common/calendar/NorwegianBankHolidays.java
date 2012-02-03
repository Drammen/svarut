package no.kommune.bergen.soa.common.calendar;

import java.util.Date;
import java.util.TreeSet;

/**
 * Fridager i Norge
 *
 * @author EinarValen@gmail.com
 */
public class NorwegianBankHolidays extends TreeSet<Date> {
	private static final long serialVersionUID = 1L;

	public NorwegianBankHolidays( int year ) {
		super();
		Easter easter = new Easter( year );
		this.addAll( CalendarHelper.weekends( year ) );
		this.add( easter.getPalmSunday() );
		this.add( easter.getMaundyThursday() );
		this.add( easter.getGoodFriday() );
		this.add( easter.getEasterDay() );
		this.add( easter.getEasterMonday() );
		this.add( easter.getAscensionDay() );
		this.add( easter.getWhitSunday() );
		this.add( easter.getWhitMonday() );
		this.add( CalendarHelper.toDate( year, 1, 1 ) );
		this.add( CalendarHelper.toDate( year, 5, 1 ) );
		this.add( CalendarHelper.toDate( year, 5, 17 ) );
		this.add( CalendarHelper.toDate( year, 12, 25 ) );
		this.add( CalendarHelper.toDate( year, 12, 26 ) );
	}
}
