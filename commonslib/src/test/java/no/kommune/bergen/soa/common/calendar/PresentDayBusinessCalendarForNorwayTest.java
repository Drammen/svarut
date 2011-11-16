package no.kommune.bergen.soa.common.calendar;

import org.junit.Assert;
import org.junit.Test;

public class PresentDayBusinessCalendarForNorwayTest {

	@Test
	public void getInstance() {
		BusinessCalendar businessCalendar = PresentDayBusinessCalendarForNorway.getInstance();
		Assert.assertNotNull( businessCalendar );
	}
}
