package no.kommune.bergen.soa.common.calendar;

import junit.framework.Assert;

import org.junit.Test;

public class BusinessCalendarTest {

	@Test
	public void testShouldHandle0PreviusWorkday(){
		Assert.assertEquals( "2012-03-01", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 1 ), 0 ) ) );
	}

	@Test
	public void testShouldHandle1PreviusWorkday(){
		Assert.assertEquals( "2012-02-29", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 1 ), 1 ) ) );
	}

	@Test
	public void testShouldHandle2PreviusWorkday(){
		Assert.assertEquals( "2012-02-28", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 1 ), 2 ) ) );
	}

	@Test
	public void testShouldHandle10PreviusWorkday(){
		Assert.assertEquals( "2012-02-16", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 1 ), 10 ) ) );
	}

	@Test
	public void testShouldHandleWeekendSaturday(){
		Assert.assertEquals( "2012-03-02", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 3 ), 0 ) ) );
	}

	@Test
	public void testShouldHandleWeekendSunday(){
		Assert.assertEquals( "2012-03-02", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 4 ), 0 ) ) );
	}

	@Test
	public void testShouldHandlePreviousWorkdayFromMonday() {
		Assert.assertEquals( "2012-03-02", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 5 ), 1 ) ) );
	}

	@Test
	public void testShouldHandlePreviousWorkdayFromFriday() {
		Assert.assertEquals( "2012-03-05", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 6 ), 1 ) ) );
	}

	@Test
	public void testShouldHandlePreviousWorkdayFromTheTuesdayAfterEaster() {
		Assert.assertEquals( "2012-04-04", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 4, 10 ), 1 ) ) );
	}

	@Test
	public void testShouldHandlePreviousWorkdayFromDecember27() {
		Assert.assertEquals( "2012-12-24", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 12, 27 ), 1 ) ) );
	}

	@Test
	public void testShouldHandleLeapYearDay() {
		Assert.assertEquals( "2012-02-29", CalendarHelper.formatAsDate( BusinessCalendar.getPreviousWorkday( CalendarHelper.toDate( 2012, 3, 2 ), 2 ) ) );
	}

}
