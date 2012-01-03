package no.kommune.bergen.soa.common.calendar;

/** @author EinarValen@gmail.com */
public class PresentDayBusinessCalendarForNorway {

	private PresentDayBusinessCalendarForNorway() {}

	public static BusinessCalendar getInstance() {
		return new BusinessCalendar();
	}

}
