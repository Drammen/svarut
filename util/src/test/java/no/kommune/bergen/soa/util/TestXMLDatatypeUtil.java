package no.kommune.bergen.soa.util;

import org.junit.Test;

import java.util.Date;

public class TestXMLDatatypeUtil {

	@Test
	public void testToXMLGregorianCalendar() {
		Date date = new Date();
		for (int i = 0; i < 500; i++)
			XMLDatatypeUtil.toXMLGregorianCalendar(date);
	}
}
