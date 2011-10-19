package no.kommune.bergen.soa.svarut.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class DispatchWindowTest {

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeSpec() {
        DispatchWindow dw = new DispatchWindow(26, 0, -1, 0);
        assertNotNull(dw);
    }

    @Test
    public void testInside() {
        DispatchWindow dw = new DispatchWindow(12, 0, 18, 0);
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,14);
        cal.set(Calendar.MINUTE,22);

        assertTrue("innenfor 1", dw.isInWindow(cal.getTime()));

        cal.set(Calendar.HOUR_OF_DAY,12);
        cal.set(Calendar.MINUTE,0);

        assertTrue("innenfor 2", dw.isInWindow(cal.getTime()));

        cal.set(Calendar.HOUR_OF_DAY,17);
        cal.set(Calendar.MINUTE,59);

        assertTrue("innenfor 3", dw.isInWindow(cal.getTime()));

        cal.set(Calendar.HOUR_OF_DAY,18);
        cal.set(Calendar.MINUTE,0);

        assertTrue("innenfor 4", dw.isInWindow(cal.getTime()));
    }

    @Test
    public void testOutside() {
        DispatchWindow dw = new DispatchWindow(12, 0, 18, 0);
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,7);
        cal.set(Calendar.MINUTE,22);

        assertFalse("utenfor 1", dw.isInWindow(cal.getTime()));

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        assertFalse("utenfor 2", dw.isInWindow(cal.getTime()));

        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        assertFalse("utenfor 2", dw.isInWindow(cal.getTime()));

        cal.set(Calendar.HOUR_OF_DAY,18);
        cal.set(Calendar.MINUTE,01);
        assertFalse("utenfor 2", dw.isInWindow(cal.getTime()));

    }

}
