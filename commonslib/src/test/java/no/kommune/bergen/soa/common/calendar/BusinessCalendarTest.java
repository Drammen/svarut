package no.kommune.bergen.soa.common.calendar;

import java.util.Date;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

/** @author einarvalen@gmail.com */
public class BusinessCalendarTest {
	private final BusinessCalendar businessCalendar = new BusinessCalendar( );

	@Test
	public void isWorkday() {
		Assert.assertTrue( this.businessCalendar.isWorkday( CalendarHelper.toDate( 2012, 1, 2 ) ) );
		Assert.assertTrue( this.businessCalendar.isWorkday( CalendarHelper.toDate( 2012, 4, 4 ) ) );
		Assert.assertFalse( this.businessCalendar.isWorkday( CalendarHelper.toDate( 2012, 4, 1 ) ) );
		Assert.assertFalse( this.businessCalendar.isWorkday( CalendarHelper.toDate( 2012, 4, 5 ) ) );
		Assert.assertFalse( this.businessCalendar.isWorkday( CalendarHelper.toDate( 2012, 12, 25 ) ) );
	}

	@Test
	public void isDayOff() {
		Assert.assertFalse( this.businessCalendar.isDayOff( CalendarHelper.toDate( 2012, 1, 2 ) ) );
		Assert.assertFalse( this.businessCalendar.isDayOff( CalendarHelper.toDate( 2012, 4, 4 ) ) );
		Assert.assertTrue( this.businessCalendar.isDayOff( CalendarHelper.toDate( 2012, 4, 1 ) ) );
		Assert.assertTrue( this.businessCalendar.isDayOff( CalendarHelper.toDate( 2012, 4, 5 ) ) );
		Assert.assertTrue( this.businessCalendar.isDayOff( CalendarHelper.toDate( 2012, 12, 25 ) ) );
	}

	@Test
	public void previousWorkdayFromSaturday() {
		Assert.assertEquals( "2012-01-06", CalendarHelper.formatAsDate( this.businessCalendar.previousWorkday( CalendarHelper.toDate( 2012, 1, 7 ) ) ) );
	}

	@Test
	public void previousWorkdayFromSunday() {
		Assert.assertEquals( "2012-01-06", CalendarHelper.formatAsDate( this.businessCalendar.previousWorkday( CalendarHelper.toDate( 2012, 1, 8 ) ) ) );
	}

	@Test
	public void previousWorkdayFromMonday() {
		Assert.assertEquals( "2012-01-06", CalendarHelper.formatAsDate( this.businessCalendar.previousWorkday( CalendarHelper.toDate( 2012, 1, 9 ) ) ) );
	}

	@Test
	public void previousWorkdayFromFriday() {
		Assert.assertEquals( "2012-01-05", CalendarHelper.formatAsDate( this.businessCalendar.previousWorkday( CalendarHelper.toDate( 2012, 1, 6 ) ) ) );
	}

	@Test
	public void previousWorkdayFromTheTuesdayAfterEaster() {
		Assert.assertEquals( "2012-04-04", CalendarHelper.formatAsDate( this.businessCalendar.previousWorkday( CalendarHelper.toDate( 2012, 4, 10 ) ) ) );
	}

	@Test
	public void previousWorkdayFromDecember27() {
		Assert.assertEquals( "2012-12-24", CalendarHelper.formatAsDate( this.businessCalendar.previousWorkday( CalendarHelper.toDate( 2012, 12, 27 ) ) ) );
	}

	@Test
	public void nextWorkdayFromSunday() {
		Assert.assertEquals( "2012-01-02", CalendarHelper.formatAsDate( this.businessCalendar.nextWorkday( CalendarHelper.toDate( 2012, 1, 1 ) ) ) );
	}

	@Test
	public void nextWorkdayFromSaturday() {
		Assert.assertEquals( "2012-01-09", CalendarHelper.formatAsDate( this.businessCalendar.nextWorkday( CalendarHelper.toDate( 2012, 1, 7 ) ) ) );
	}

	@Test
	public void nextWorkdayFromMonday() {
		Assert.assertEquals( "2012-01-03", CalendarHelper.formatAsDate( this.businessCalendar.nextWorkday( CalendarHelper.toDate( 2012, 1, 2 ) ) ) );
		}

	@Test
	public void nextWorkdayFromFriday() {
		Assert.assertEquals( "2012-01-09", CalendarHelper.formatAsDate( this.businessCalendar.nextWorkday( CalendarHelper.toDate( 2012, 1, 6 ) ) ) );
	}

	@Test
	public void nextWorkdayFromTheWednesdayBeforeEaster() {
		Assert.assertEquals( "2012-04-10", CalendarHelper.formatAsDate( this.businessCalendar.nextWorkday( CalendarHelper.toDate( 2012, 4, 4 ) ) ) );
	}

	@Test
	public void nextWorkdayFromTheDayBeforeXmas() {
		Assert.assertEquals( "2012-12-27", CalendarHelper.formatAsDate( this.businessCalendar.nextWorkday( CalendarHelper.toDate( 2012, 12, 24 ) ) ) );
	}

	@Test
	public void previousDayOffFromSunday() {
		Assert.assertEquals( "2012-01-07", CalendarHelper.formatAsDate( this.businessCalendar.previousDayOff( CalendarHelper.toDate( 2012, 1, 8 ) ) ) );
	}

	@Test
	public void previousDayOffFromSaturday() {
		Assert.assertEquals( "2012-01-01", CalendarHelper.formatAsDate( this.businessCalendar.previousDayOff( CalendarHelper.toDate( 2012, 1, 7 ) ) ) );
	}

	@Test
	public void previousDayOffFromMonday() {
		Assert.assertEquals( "2012-01-01", CalendarHelper.formatAsDate( this.businessCalendar.previousDayOff( CalendarHelper.toDate( 2012, 1, 2 ) ) ) );
	}

	@Test
	public void previousDayOffFromFriday() {
		Assert.assertEquals( "2012-01-01", CalendarHelper.formatAsDate( this.businessCalendar.previousDayOff( CalendarHelper.toDate( 2012, 1, 6 ) ) ) );
	}

	@Test
	public void previousDayOffFromGoodFriday() {
		Assert.assertEquals( "2012-04-05", CalendarHelper.formatAsDate( this.businessCalendar.previousDayOff( CalendarHelper.toDate( 2012, 4, 6 ) ) ) );
	}

	@Test
	public void previousDayOffFromTheDayAfterXmas() {
		Assert.assertEquals( "2012-12-25", CalendarHelper.formatAsDate( this.businessCalendar.previousDayOff( CalendarHelper.toDate( 2012, 12, 26 ) ) ) );
	}

	@Test
	public void nextDayOffFromSunday() {
		Assert.assertEquals( "2012-01-07", CalendarHelper.formatAsDate( this.businessCalendar.nextDayOff( CalendarHelper.toDate( 2012, 1, 1 ) ) ) );
	}

	@Test
	public void nextDayOffFromSaturday() {
		Assert.assertEquals( "2012-01-08", CalendarHelper.formatAsDate( this.businessCalendar.nextDayOff( CalendarHelper.toDate( 2012, 1, 7 ) ) ) );
	}

	@Test
	public void nextDayOffFromMonday() {
		Assert.assertEquals( "2012-01-07", CalendarHelper.formatAsDate( this.businessCalendar.nextDayOff( CalendarHelper.toDate( 2012, 1, 2 ) ) ) );
	}

	@Test
	public void nextDayOffFromFriday() {
		Assert.assertEquals( "2012-01-07", CalendarHelper.formatAsDate( this.businessCalendar.nextDayOff( CalendarHelper.toDate( 2012, 1, 6 ) ) ) );
	}

	@Test
	public void nextDayOffFromTheWednesdayBeforeEaster() {
		Assert.assertEquals( "2012-04-05", CalendarHelper.formatAsDate( this.businessCalendar.nextDayOff( CalendarHelper.toDate( 2012, 4, 4 ) ) ) );
	}

	@Test
	public void nextDayOffFromTheDayBeforeXmas() {
		Assert.assertEquals( "2012-12-25", CalendarHelper.formatAsDate( this.businessCalendar.nextDayOff( CalendarHelper.toDate( 2012, 12, 24 ) ) ) );
	}

}
