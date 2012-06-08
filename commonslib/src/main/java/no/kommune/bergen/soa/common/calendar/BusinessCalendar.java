package no.kommune.bergen.soa.common.calendar;

import java.util.Date;
import java.util.Set;

import org.joda.time.LocalDate;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.util.CalendarUtil;

public class BusinessCalendar {

	private static HolidayManager holidayManager = HolidayManager.getInstance(HolidayCalendar.NORWAY);

	public static Date getPreviousWorkday(int workdays){
		return getPreviousWorkday(new Date(), workdays);
	}

	public static Date getPreviousWorkday(Date date, int workdays){
		date = getFirstPreviousWorkday(date);
		for(int i=workdays; i>0; i--){
			date = getFirstPreviousWorkday(CalendarHelper.addDays( date, -1 ));
		}
		return date;
	}

	private static Date getFirstPreviousWorkday(Date date) {
		while (isDayOff( date )) {
			date = CalendarHelper.addDays( date, -1 );
		}
		return date;
	}

	private static boolean isDayOff( Date date ) {
		LocalDate localDate = new LocalDate(date);
		boolean isHoliday = isHoliday(localDate);
		boolean isWeekend = CalendarUtil.isWeekend(localDate);
		return isHoliday || isWeekend;
	}

	private static boolean isHoliday(LocalDate localDate) {
		//This workaround is reported to the jollyday team and can be changed to holidayManager.isHoliday(...) when they fix it in jollyday version 0.5.0
		Set<Holiday> holidays = holidayManager.getHolidays(localDate.getYear());
		for (Holiday h : holidays) {
			LocalDate holidayDate = h.getDate();
			if(holidayDate.isEqual(localDate)){
				return true;
			}
		}
		return false;
	}

}
