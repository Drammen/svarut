package no.kommune.bergen.soa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/** Date conversion utility for use with XML */
public class XMLDatatypeUtil {
	private static DatatypeFactory datatypeFactory;

	private static DatatypeFactory getFactory() {
		if (datatypeFactory == null) {
			try {
				datatypeFactory = DatatypeFactory.newInstance();
			} catch (DatatypeConfigurationException e) {
				throw new RuntimeException( e );
			}
		}
		return datatypeFactory;
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar( Date date ) {
		if (date == null) {
			return null;
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime( date );
		return getFactory().newXMLGregorianCalendar( cal );
	}

	public static XMLGregorianCalendar createCalendar( String date, String format ) {
		SimpleDateFormat df = new SimpleDateFormat( format );
		try {
			return toXMLGregorianCalendar( df.parse( date ) );
		} catch (ParseException e) {
			throw new RuntimeException( e );
		}
	}

	public static XMLGregorianCalendar createCalendar( String date ) {
		return createCalendar( date, "yyyy-MM-dd" );
	}

	public static XMLGregorianCalendar toXMLGregorianCalendar( Calendar cal ) {
		return XMLDatatypeUtil.toXMLGregorianCalendar( cal.getTime() );
	}

	public static Date toDate( XMLGregorianCalendar xmlGregorianCalendar ) {
		if (xmlGregorianCalendar == null) return null;
		return xmlGregorianCalendar.toGregorianCalendar().getTime();
	}
}
