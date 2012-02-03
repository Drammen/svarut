package no.kommune.bergen.soa.common.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/** @author einarvalen@gmail.com */
public class CalendarHelperTest {

	@Test
	public void toDate() {
		Assert.assertEquals( "1978-07-13 00:00:00", CalendarHelper.formatAsDateAndTime( CalendarHelper.toDate( 1978, 7, 13 ) ) );
		Assert.assertEquals( "1982-08-30 00:00:00", CalendarHelper.formatAsDateAndTime( CalendarHelper.toDate( 1982, 8, 30 ) ) );
		Assert.assertEquals( "2000-02-29 00:00:00", CalendarHelper.formatAsDateAndTime( CalendarHelper.toDate( 2000, 2, 29 ) ) );
		Assert.assertEquals( "2001-03-01 00:00:00", CalendarHelper.formatAsDateAndTime( CalendarHelper.toDate( 2001, 2, 29 ) ) );
		Assert.assertEquals( "2011-04-03 00:00:00", CalendarHelper.formatAsDateAndTime( CalendarHelper.toDate( 2011, 3, 34 ) ) );
	}

	@Test
	public void extractYear() {
		Assert.assertEquals( 2011, CalendarHelper.extractYear( CalendarHelper.toDate( 2011, 3, 4 ) ) );
		Assert.assertEquals( 2012, CalendarHelper.extractYear( CalendarHelper.toDate( 2012, 11, 12 ) ) );
		Assert.assertEquals( 3011, CalendarHelper.extractYear( CalendarHelper.toDate( 3011, 3, 4 ) ) );
	}

	@Test
	public void extractMonth() {
		Assert.assertEquals( 3, CalendarHelper.extractMonth( CalendarHelper.toDate( 2011, 3, 4 ) ) );
		Assert.assertEquals( 11, CalendarHelper.extractMonth( CalendarHelper.toDate( 2012, 11, 12 ) ) );
		Assert.assertEquals( 3, CalendarHelper.extractMonth( CalendarHelper.toDate( 3011, 3, 4 ) ) );
	}

	@Test
	public void extractDayOfMonth() {
		Assert.assertEquals( 4, CalendarHelper.extractDayOfMonth( CalendarHelper.toDate( 2011, 3, 4 ) ) );
		Assert.assertEquals( 12, CalendarHelper.extractDayOfMonth( CalendarHelper.toDate( 2012, 11, 12 ) ) );
		Assert.assertEquals( 4, CalendarHelper.extractDayOfMonth( CalendarHelper.toDate( 3011, 3, 4 ) ) );
	}

	@Test
	public void extractTimePart() {
		Date now = new Date();
		Date dayOne = new Date( CalendarHelper.extractTimePart( now ) );
		Assert.assertEquals( CalendarHelper.formatAsTime( now ), CalendarHelper.formatAsTime( dayOne ) );
		Assert.assertFalse( CalendarHelper.formatAsDateAndTime( now ).equals( CalendarHelper.formatAsDateAndTime( dayOne ) ) );
		Assert.assertEquals( "1970-01-01", CalendarHelper.formatAsDate( dayOne ) );
	}

	@Test
	public void clearTimepart() {
		Assert.assertEquals( "00:00:00", CalendarHelper.formatAsTime( CalendarHelper.clearTimePart( new Date() ) ) );
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set( 2011, 0, 22, 13, 39, 14 );
		Assert.assertEquals( "2011-01-22 00:00:00", CalendarHelper.formatAsDateAndTime( CalendarHelper.clearTimePart( calendar.getTime() ) ) );
	}

	@Test
	public void addDays() {
		Date date = CalendarHelper.toDate( 2011, 11, 15 );
		Assert.assertEquals( "2011-11-17", CalendarHelper.formatAsDate( CalendarHelper.addDays( date, 2 ) ) );
		Assert.assertEquals( "2011-12-17", CalendarHelper.formatAsDate( CalendarHelper.addDays( date, 32 ) ) );
		Assert.assertEquals( "2011-10-15", CalendarHelper.formatAsDate( CalendarHelper.addDays( date, -31 ) ) );
	}

	@Test
	public void weekendsIn2012() {
		int year = 2012;
		String[] datesToLookFor = { "2012-01-01", "2012-01-07", "2012-01-08", "2012-03-17", "2012-03-18", "2012-12-29", "2012-12-30", "2012-08-18", "2012-08-19" };
		verifyWeekends( year, CalendarHelper.weekends( year ), datesToLookFor );
	}

	@Test
	public void weekendsIn2011() {
		int year = 2011;
		String[] datesToLookFor = { "2011-01-01", "2011-01-02", "2011-01-08", "2011-01-09", "2011-05-14", "2011-05-15", "2011-12-24", "2011-12-25", "2011-12-31" };
		verifyWeekends( year, CalendarHelper.weekends( year ), datesToLookFor );
	}

	@Test
	public void weekendsIn2016() {
		int year = 2016;
		String[] datesToLookFor = { "2016-01-02", "2016-01-03", "2016-01-09", "2016-01-10", "2016-07-16", "2016-07-17", "2016-12-24", "2016-12-25", "2016-12-31" };
		verifyWeekends( year, CalendarHelper.weekends( year ), datesToLookFor );
	}

	private void verifyWeekends( int year, Set<Date> weekends, String[] datesToLookFor ) {
		int countOfFoundDatesToLookFor = 0;
		for (Date date : weekends) {
			for (String dateToLookFor : datesToLookFor) {
				if (dateToLookFor.equals( CalendarHelper.formatAsDate( date ) )) countOfFoundDatesToLookFor++;
			}
			Assert.assertEquals( year, CalendarHelper.extractYear( date ) );
		}
		Assert.assertEquals( datesToLookFor.length, countOfFoundDatesToLookFor );
	}

	@Test
	public void isWithinBounds() {
		Date jan_1_2011 = CalendarHelper.toDate( 2011, 1, 1 );
		Date dec_31_2011 = CalendarHelper.toDate( 2011, 12, 31 );
		Date dec_30_2011 = CalendarHelper.toDate( 2011, 12, 30 );
		Date jan_1_2012 = CalendarHelper.toDate( 2012, 1, 1 );
		Date dec_31_2010 = CalendarHelper.toDate( 2010, 12, 31 );
		Assert.assertTrue( CalendarHelper.isWithinBounds( jan_1_2011, dec_31_2011, dec_30_2011 ) );
		Assert.assertTrue( CalendarHelper.isWithinBounds( jan_1_2011, dec_31_2011, dec_31_2011 ) );
		Assert.assertTrue( CalendarHelper.isWithinBounds( jan_1_2011, dec_31_2011, jan_1_2011 ) );
		Assert.assertFalse( CalendarHelper.isWithinBounds( jan_1_2011, dec_31_2011, jan_1_2012 ) );
		Assert.assertFalse( CalendarHelper.isWithinBounds( jan_1_2011, dec_31_2011, dec_31_2010 ) );
	}

}
