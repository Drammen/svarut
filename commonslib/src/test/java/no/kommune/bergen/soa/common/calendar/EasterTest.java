package no.kommune.bergen.soa.common.calendar;

import java.util.Date;

import junit.framework.Assert;

import no.kommune.bergen.soa.common.calendar.CalendarHelper;
import no.kommune.bergen.soa.common.calendar.Easter;

import org.junit.Test;

/** @author einarvalen@gmail.com */
public class EasterTest {

	@Test
	public void easterDay() {
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 4, 8 ) ), fmt( newEaster( 2012 ).getEasterDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 4, 24 ) ), fmt( newEaster( 2011 ).getEasterDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 1978, 3, 26 ) ), fmt( newEaster( 1978 ).getEasterDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2048, 4, 5 ) ), fmt( newEaster( 2048 ).getEasterDay() ) );
	}

	@Test
	public void easterDerivedHolidays2012() {
		Easter easter = newEaster( 2012 );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 4, 8 ) ), fmt( easter.getEasterDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 4, 9 ) ), fmt( easter.getEasterMonday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 4, 6 ) ), fmt( easter.getGoodFriday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 4, 5 ) ), fmt( easter.getMaundyThursday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 4, 1 ) ), fmt( easter.getPalmSunday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 5, 17 ) ), fmt( easter.getAscensionDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 5, 27 ) ), fmt( easter.getWhitSunday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2012, 5, 28 ) ), fmt( easter.getWhitMonday() ) );
	}

	@Test
	public void easterDerivedHolidays2011() {
		Easter easter = newEaster( 2011 );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 4, 24 ) ), fmt( easter.getEasterDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 4, 25 ) ), fmt( easter.getEasterMonday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 4, 22 ) ), fmt( easter.getGoodFriday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 4, 21 ) ), fmt( easter.getMaundyThursday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 4, 17 ) ), fmt( easter.getPalmSunday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 6, 2 ) ), fmt( easter.getAscensionDay() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 6, 12 ) ), fmt( easter.getWhitSunday() ) );
		Assert.assertEquals( fmt( CalendarHelper.toDate( 2011, 6, 13 ) ), fmt( easter.getWhitMonday() ) );
	}

	private String fmt( Date d ) {
		return CalendarHelper.formatAsDate( d );
	}

	private Easter newEaster( int year ) {
		return new Easter( year );
	}
}
