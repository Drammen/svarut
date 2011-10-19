package no.kommune.bergen.soa.svarut.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DispatchWindow {

    private int startHour;
    private int startMinute;
    private int stopHour;
    private int stopMinute;

    public DispatchWindow(int startHour, int startMinute, int stopHour, int stopMinute) {

        if ( startHour < 0 || startHour > 23 ) throw new IllegalArgumentException("ugyldig startHour: " + startHour);
        if ( stopHour < 0 || stopHour > 23 ) throw new IllegalArgumentException("ugyldig stopHour: " + stopHour);
        if ( startMinute < 0 || startMinute > 60 ) throw new IllegalArgumentException("ugyldig startMinute: " + startMinute);
        if ( stopMinute < 0 || stopMinute > 60 ) throw new IllegalArgumentException("ugyldig stopMinute: " + stopMinute);

        if ( stopHour < startHour ) throw new IllegalArgumentException("stopHour must be greater than startHour");
        if (( stopHour == startHour) && (stopMinute < startMinute)) throw new IllegalArgumentException("stopMinute must be greater than startMinute in the same hour");

        this.startHour = startHour;
        this.startMinute = startMinute;
        this.stopHour = stopHour;
        this.stopMinute = stopMinute;
    }

    public boolean isInWindow(Date tidspunkt) {

        Calendar gcal = GregorianCalendar.getInstance();
        gcal.setTime(tidspunkt);

        final int hour = gcal.get(Calendar.HOUR_OF_DAY);
        final int minute = gcal.get(Calendar.MINUTE);

        if (!(startHour <= hour && hour <= stopHour)) return false;
        if ( hour == startHour && minute < startMinute ) return false;
        if ( hour == stopHour && minute > stopMinute ) return false;

        return true;

    }

}
