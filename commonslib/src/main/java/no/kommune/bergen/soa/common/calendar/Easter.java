package no.kommune.bergen.soa.common.calendar;

import java.util.Date;

/**
 * Helligdager som er relatert til påske.
 * @author einarvalen@gmail.com
 */
public class Easter {
	private final Date easterDay;

	public Easter( int year ) {
		this.easterDay = this.calculateEasterDate( year );
	}

	public Easter() {
		this( CalendarHelper.extractYear( new Date() ) );
	}

	public Date getPalmSunday() {
		return CalendarHelper.addDays( this.easterDay, -7 );
	}

	public Date getMaundyThursday() {
		return CalendarHelper.addDays( this.easterDay, -3 );
	}

	public Date getGoodFriday() {
		return CalendarHelper.addDays( this.easterDay, -2 );
	}

	public Date getEasterDay() {
		return this.easterDay;
	}

	public Date getEasterMonday() {
		return CalendarHelper.addDays( this.easterDay, 1 );
	}

	public Date getAscensionDay() {
		return CalendarHelper.addDays( this.easterDay, 39 );
	}

	public Date getWhitSunday() {
		return CalendarHelper.addDays( this.easterDay, 7 * 7 );
	}

	public Date getWhitMonday() {
		return CalendarHelper.addDays( this.easterDay, 7 * 7 + 1 );
	}

	private Date calculateEasterDate( int year ) {
		return createDate( year, findNextSunday( year, calculatePaschalFullMoon( year ) ) );
	}

	private Date createDate( int year, int day ) {
		int month = 3;
		if (day > 31) {
			day -= 31;
			month = 4;
		}
		return CalendarHelper.toDate( year, month, day );
	}

	private int findNextSunday( int year, int pfm ) {
		int a = (pfm - 19) % 7;
		int b = (40 - year / 100) % 4;
		if (b == 3) b += 1;
		if (b > 1) b += 1;
		int temp = year % 100;
		int c = (temp + temp / 4) % 7;
		int d = ((20 - a - b - c) % 7) + 1;
		return pfm + d;
	}

	private int calculatePaschalFullMoon( int year ) {
		int yearDiv100 = year / 100;
		int yearMod19 = year % 19;
		int temp = (yearDiv100 - 15) / 2 + 202 - 11 * yearMod19;
		for (int i : new int[] { 21, 24, 25, 27, 28, 29, 30, 31, 32, 34, 35, 38 }) {
			if (i == yearDiv100) temp -= 1;
		}
		for (int i : new int[] { 33, 36, 37, 39, 40 }) {
			if (i == yearDiv100) temp -= 2;
		}
		temp %= 30;
		int pfm = temp + 21;
		if (temp == 29) pfm -= 1;
		if ((temp == 28 && yearMod19 > 10)) pfm -= 1;
		return pfm;
	}

}
