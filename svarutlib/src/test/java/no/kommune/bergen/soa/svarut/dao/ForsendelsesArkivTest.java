package no.kommune.bergen.soa.svarut.dao;

import static no.kommune.bergen.soa.svarut.dto.ShipmentPolicy.ALTINN_OG_APOST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.AccessControlException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import no.kommune.bergen.soa.common.calendar.BusinessCalendar;
import no.kommune.bergen.soa.common.calendar.CalendarHelper;
import no.kommune.bergen.soa.common.pdf.PdfGeneratorImpl;
import no.kommune.bergen.soa.svarut.JdbcHelper;
import no.kommune.bergen.soa.svarut.ServiceContext;
import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.Orgnr;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
import no.kommune.bergen.soa.svarut.domain.Printed;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.svarut.util.FilHenter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ForsendelsesArkivTest {
	private static final int MOCK_RECEIPT_ID = 123456;
	public static final String fnr = "12345678901";
	public static final String orgnr = "987654321";
	private static final String navn = "navn";
	private static final String adresse1 = "adresse1";
	private static final String adresse2 = "adresse2";
	private static final String adresse3 = "adresse3";
	private static final String postnr = "9999";
	private static final String poststed = "poststed";
	private static final String avsender_navn = "avsender_navn";
	private static final String avsender_adresse1 = "avsender_adresse1";
	private static final String avsender_adresse2 = "avsender_adresse2";
	private static final String avsender_adresse3 = "avsender_adresse3";
	private static final String avsender_postnr = "9998";
	private static final String avsender_poststed = "avsender_poststed";
	private static final String land = "land";
	private static final String tittel = "tittel";
	private static final String melding = "melding";
	private static final String appid = "appid";
	private ForsendelsesArkiv forsendelsesArkiv;
	public static final String testPdf = "test.pdf";

	@Before
	public void init() {
		// forsendelsesArkiv = createForsendesesArkivOracle();
		forsendelsesArkiv = createForsendesesArkiv();
	}

	public static ForsendelsesArkiv createForsendesesArkiv() {
		JdbcHelper jdbcHelper = new JdbcHelper();
		jdbcHelper.createTable( "FORSENDELSESARKIV" );
		FileStore fileStore = new FileStore("target", new PdfGeneratorImpl("target"));
		return new ForsendelsesArkiv( fileStore, jdbcHelper.getJdbcTemplate() );
	}

	@Test
	public void testTestPdf() {
		File fil = FilHenter.getFileAsFile( ForsendelsesArkivTest.testPdf );
		assertTrue( fil.exists() );
	}

	@Test
	public void retrieveYoungerThan() {
		int numberOfDays = 3;
		Forsendelse f = createOldForsendelse( numberOfDays );
		assertNotNull( f );
		assertEquals(0, forsendelsesArkiv.retrieveYoungerThan(0, null).size());
		assertEquals(0, forsendelsesArkiv.retrieveYoungerThan(1, null).size());
		assertEquals(0, forsendelsesArkiv.retrieveYoungerThan(2, null).size());
		assertEquals(0, forsendelsesArkiv.retrieveYoungerThan(3, null).size());
		assertEquals(1, forsendelsesArkiv.retrieveYoungerThan(4, null).size());
		assertEquals(1, forsendelsesArkiv.retrieveYoungerThan(5, null).size());
	}

	@Test
	public void retrieveYoungerThanAgain() {
		for (int numberOfDays = 0; numberOfDays < 4; numberOfDays++) {
			createOldForsendelse( numberOfDays );
		}
		assertEquals(0, forsendelsesArkiv.retrieveYoungerThan(0, null).size());
		assertEquals(1, forsendelsesArkiv.retrieveYoungerThan(1, null).size());
		assertEquals(2, forsendelsesArkiv.retrieveYoungerThan(2, null).size());
		assertEquals(3, forsendelsesArkiv.retrieveYoungerThan(3, null).size());
		assertEquals(4, forsendelsesArkiv.retrieveYoungerThan(4, null).size());
		assertEquals(4, forsendelsesArkiv.retrieveYoungerThan(5, null).size());
	}

	private Forsendelse createOldForsendelse( int numberOfDays ) {
		String id = forsendelsesArkiv.save(createForsendelse(numberOfDays), getTestDocument());
		assertNotNull( id );
		forsendelsesArkiv.jdbcTemplate.update("UPDATE FORSENDELSESARKIV SET SENDT=? WHERE ID=?", new Object[]{daysAgo(numberOfDays), id}, new int[]{Types.DATE, Types.VARCHAR});
		return forsendelsesArkiv.retrieve( id );
	}

	private java.sql.Date daysAgo( int numberOfDays ) {
		final long dayInMillis = 1000 * 60 * 60 * 24;
		long now = System.currentTimeMillis();
		return new java.sql.Date( now - (dayInMillis * numberOfDays) );
	}

	@Test
	public void saveThenRetrieve() {
		int variant = 1;
		String id = forsendelsesArkiv.save( createForsendelse( variant ), getTestDocument() );
		assertNotNull( id );
		Forsendelse f = forsendelsesArkiv.retrieve( id );
		validateForsendelse( f, variant );
	}

	@Test
	public void saveThenRetrieveSeveralOrg() {
		saveThenRetrieveSeveral( new Orgnr( orgnr ) );
	}

	@Test
	public void saveThenRetrieveSeveralFnr() {
		saveThenRetrieveSeveral( new Fodselsnr( fnr ) );
	}

	private void saveThenRetrieveSeveral( JuridiskEnhet juridiskEnhet ) {
		final int count = 4;
		for (int variant = 1; variant <= count; variant++) {
			String id = forsendelsesArkiv.save( createForsendelse( variant ), getTestDocument() );
			assertNotNull( id );
		}
		List<Forsendelse> forsendelser = forsendelsesArkiv.retrieveList( juridiskEnhet );
		assertEquals( count, forsendelser.size() );
		for (Forsendelse f : forsendelser) {
			validateForsendelse( f, getVariant( f ) );
		}
	}

	public static int getVariant( Forsendelse f ) {
		return Integer.parseInt(f.getNavn().substring(navn.length()));
	}

	@Test
	public void setPrinted() {
		int variant = 1;
		String id = forsendelsesArkiv.save( createForsendelse( variant ), getTestDocument() );
		assertNotNull( id );
		PrintReceipt printReceipt = newPrintReceipt();
		forsendelsesArkiv.setPrinted( id, printReceipt );
		Forsendelse forsendelse = forsendelsesArkiv.retrieve( id );
		assertEquals( printReceipt.getPrintId(), forsendelse.getPrintId() );
		assertEquals( printReceipt.getPageCount(), forsendelse.getAntallSider() );
	}

	@Test
	public void readUnsent() {
		int[] variants = { 0, 1, 2, 3, 4 };
		String ids[] = new String[variants.length];
		for (int i = 0; i < variants.length; i++) {
			ids[i] = forsendelsesArkiv.save( createForsendelse( i ), getTestDocument() );
		}
		List<String> list = forsendelsesArkiv.readUnsent( null );
		assertEquals( variants.length, list.size() );
		PrintReceipt printReceipt = newPrintReceipt();
		forsendelsesArkiv.setPrinted( ids[0], printReceipt );
		list = forsendelsesArkiv.readUnsent( null );
		assertEquals( variants.length - 1, list.size() );
		forsendelsesArkiv.setSentAltinn(ids[1], MOCK_RECEIPT_ID);
		list = forsendelsesArkiv.readUnsent( null );
		assertEquals( variants.length - 2, list.size() );
	}

	@Test
	public void readThenSetUnRead() {
		String id = forsendelsesArkiv.save( createForsendelse( 1 ), getTestDocument() );
		Map<?, ?> row = forsendelsesArkiv.retrieveRow( id );
		assertNull( row.get( "LEST" ) );
		forsendelsesArkiv.confirm( id );
		row = forsendelsesArkiv.retrieveRow( id );
		assertNotNull( row.get( "LEST" ) );
		forsendelsesArkiv.setUnread( id );
		row = forsendelsesArkiv.retrieveRow( id );
		assertNull( row.get( "LEST" ) );
	}

	@Test
	public void calculateTimeLimit() {
		long days = 365 * 10;
		long now = System.currentTimeMillis();
		long daysInMillis = days * 1000 * 60 * 60 * 24;
		long expect = now - daysInMillis;
		Timestamp timeLimit = forsendelsesArkiv.calculateTimeLimit( days );
		expectWithinRange( expect, expect + 1000, timeLimit.getTime() );
	}

	private void expectWithinRange( long lower, long upper, long compareTo ) {
		Assert.assertTrue( compareTo >= lower );
		Assert.assertTrue( compareTo <= upper );
	}

	@Test
	@Ignore
	public void readThenRemove() {
		String id = forsendelsesArkiv.save( createForsendelse( 1 ), getTestDocument() );
		Map<?, ?> row = forsendelsesArkiv.retrieveRow( id );
		assertNull( row.get( "LEST" ) );
		forsendelsesArkiv.confirm( id );
		assertEquals( 0, forsendelsesArkiv.removeOlderThan( 1 ) );
		assertEquals( 1, forsendelsesArkiv.removeOlderThan( -1 ) );
		try {
			forsendelsesArkiv.retrieveRow( id );
			fail( "Remove failed" );
		} catch (Exception e) {
			assertTrue( true );
		}
	}

	@Test
	@Ignore
	public void removeUnreachable() {
		Forsendelse f = createForsendelse( 1 );
		f.setFnr( null );
		f.setOrgnr( null );
		String id = forsendelsesArkiv.save( f, getTestDocument() );
		Map<?, ?> row = forsendelsesArkiv.retrieveRow( id );
		assertNotNull( row );
		forsendelsesArkiv.confirm( id );
		assertEquals( 0, forsendelsesArkiv.removeUnreachable( 1 ) );
		assertEquals( 1, forsendelsesArkiv.removeUnreachable( -1 ) );
		try {
			forsendelsesArkiv.retrieveRow( id );
			fail( "Remove failed" );
		} catch (Exception e) {
			assertTrue( true );
		}
		id = forsendelsesArkiv.save( f, getTestDocument() );
		row = forsendelsesArkiv.retrieveRow( id );
		assertNotNull( row );
		forsendelsesArkiv.stop( id );
		assertEquals( 0, forsendelsesArkiv.removeUnreachable( 1 ) );
		assertEquals( 1, forsendelsesArkiv.removeUnreachable( -1 ) );
		try {
			forsendelsesArkiv.retrieveRow( id );
			fail( "Remove failed" );
		} catch (Exception e) {
			assertTrue( true );
		}
	}

	@Test
	public void authorizeFodselsnrOk() {
		Forsendelse f = createForsendelse( 1 );
		String id = forsendelsesArkiv.save( f, getTestDocument() );
		try {
			forsendelsesArkiv.authorize( id, new Fodselsnr( f.getFnr() ) );
		} catch (AccessControlException e) {
			fail( e.getMessage() );
		}
	}

	@Test(expected = AccessControlException.class)
	public void authorizeFodselsnrFail() {
		Forsendelse f = createForsendelse( 1 );
		String id = forsendelsesArkiv.save( f, getTestDocument() );
		forsendelsesArkiv.authorize( id, new Fodselsnr( "00000000000" ) );
	}

	@Test
	public void authorizeOrgnrOk() {
		Forsendelse f = createForsendelse( 1 );
		String id = forsendelsesArkiv.save( f, getTestDocument() );
		assertNotNull( id );
	}

	@Test(expected = AccessControlException.class)
	public void authorizeOrgnrFail() {
		Forsendelse f = createForsendelse( 1 );
		String id = forsendelsesArkiv.save( f, getTestDocument() );
		forsendelsesArkiv.authorize( id, new Orgnr( "000000000" ) );
	}

	@Test
	public void sequence() {
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		ServiceContext serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		ForsendelsesArkiv forsendelsesArkiv2 = serviceContext.getForsendelsesArkiv();
		String nextVal = forsendelsesArkiv2.getIdentifier();
		assertEquals( 36, nextVal.length() );
	}

	@Test
	public void updatePrinted() {
		Printed printed = newPrinted( storeNewForsendelse() );
		forsendelsesArkiv.updatePrinted( printed );
		Forsendelse forsendelse = forsendelsesArkiv.retrieve( printed.getForsendelsesId() );
		assertEquals( printed.getTidspunktPostlagt().getTime(), forsendelse.getTidspunktPostlagt().getTime() );
		assertEquals( printed.getAntallSiderPostlagt(), forsendelse.getAntallSiderPostlagt() );
	}

	@Test
	public void failedToPrintAlertWindowStartDayOverWeekend() {
		Date now = CalendarHelper.toDate( 2011, 11, 14 );
		int failedToPrintAlertWindowStartDay = 1;
		Date failedToPrintAlertWindowStart = BusinessCalendar.getPreviousWorkday( now, failedToPrintAlertWindowStartDay );
		Assert.assertEquals( "2011-11-11", CalendarHelper.formatAsDate( failedToPrintAlertWindowStart ) );
	}

	@Test
	public void testAnsvarsstedInsert() throws Exception {
		Forsendelse f = new Forsendelse();
		f.setNavn("Test Testesen");
		f.setMeldingsTekst("Hei fra SvarUtItest");
		f.setTittel("Viktig melding!");
		f.setEmail("test@hudson.iktofu.no");
		f.setAnsvarsSted("EnKulPlass");
		f.setKonteringkode("kontkode123");
		String id = forsendelsesArkiv.save(f, new ByteArrayInputStream(new byte[]{}));
		Forsendelse f2 = forsendelsesArkiv.retrieve(id);
		assertEquals("EnKulPlass", f2.getAnsvarsSted());
		assertEquals("kontkode123", f2.getKonteringkode());
	}

	private PrintReceipt newPrintReceipt() {
		PrintReceipt printReceipt = new PrintReceipt();
		printReceipt.setPrintId( "MyPrintId" );
		printReceipt.setPageCount( 123 );
		return printReceipt;
	}

	private String storeNewForsendelse() {
		Forsendelse f = createForsendelse( 1 );
		return forsendelsesArkiv.save( f, getTestDocument() );
	}

	private Printed newPrinted( String forsendelsesId ) {
		Printed printed = new Printed();
		printed.setTidspunktPostlagt( new Date() );
		printed.setAntallSiderPostlagt( 999 );
		printed.setForsendelsesId( forsendelsesId );
		return printed;
	}

	public static Forsendelse createForsendelse( int variant ) {
		return createForsendelse( variant, fnr, orgnr );
	}

	public static Forsendelse createForsendelse( int variant, String fnr, String orgnr ) {
		Forsendelse f = new Forsendelse();
		f.setFnr( fnr );
		f.setOrgnr( orgnr );
		f.setNavn( navn + variant );
		f.setAdresse1( adresse1 + variant );
		f.setAdresse2( adresse2 + variant );
		f.setAdresse3( adresse3 + variant );
		f.setPostnr( postnr + variant );
		f.setPoststed( poststed + variant );
		f.setLand( land + variant );
		f.setAvsenderNavn( avsender_navn + variant );
		f.setAvsenderAdresse1( avsender_adresse1 + variant );
		f.setAvsenderAdresse2( avsender_adresse2 + variant );
		f.setAvsenderAdresse3( avsender_adresse3 + variant );
		f.setAvsenderPostnr( avsender_postnr + variant );
		f.setAvsenderPoststed( avsender_poststed + variant );
		f.setTittel( tittel + variant );
		f.setMeldingsTekst( melding + variant );
		f.setAppid( appid + variant );
		f.setShipmentPolicy( ALTINN_OG_APOST.value() );
		return f;
	}

	public static void validateForsendelse( Forsendelse f, int variant ) {
		validateForsendelse( f, variant, fnr, orgnr );
	}

	public static void validateForsendelse( Forsendelse f, int variant, String fnr, String orgnr ) {
		assertEquals( "fnr", fnr, f.getFnr() );
		assertEquals( "orgnr", orgnr, f.getOrgnr() );
		assertEquals( "navn", (navn + variant), f.getNavn() );
		assertEquals( "adresse1", (adresse1 + variant), f.getAdresse1() );
		assertEquals( "adresse2", (adresse2 + variant), f.getAdresse2() );
		assertEquals( "adresse3", (adresse3 + variant), f.getAdresse3() );
		assertEquals( "postnr", (postnr + variant), f.getPostnr() );
		assertEquals( "poststed", (poststed + variant), f.getPoststed() );
		assertEquals( "land", (land + variant), f.getLand() );
		assertEquals( "avsender_navn", (avsender_navn + variant), f.getAvsenderNavn() );
		assertEquals( "avsender_adresse1", (avsender_adresse1 + variant), f.getAvsenderAdresse1() );
		assertEquals( "avsender_adresse2", (avsender_adresse2 + variant), f.getAvsenderAdresse2() );
		assertEquals( "avsender_adresse3", (avsender_adresse3 + variant), f.getAvsenderAdresse3() );
		assertEquals( "avsender_postnr", (avsender_postnr + variant), f.getAvsenderPostnr() );
		assertEquals( "avsender_poststed", (avsender_poststed + variant), f.getAvsenderPoststed() );
		assertEquals( "tittel", (tittel + variant), f.getTittel() );
		assertEquals( "melding", (melding + variant), f.getMeldingsTekst() );
		assertEquals( "appid", (appid + variant), f.getAppid() );
		assertNotNull( "file == null", f.getFile().getName() );
	}

	public static InputStream getTestDocument() {
		return FilHenter.getFileAsInputStream( testPdf );
	}

	@Test
	public void testMarkMessageFailed() throws Exception {
		Forsendelse f = createForsendelse( 1, "999", null );
		f = forsendelsesArkiv.retrieve( forsendelsesArkiv.save( f, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) ) );
		Date nesteforsok = new Date( f.getSendt().getTime() + 5000L );
		forsendelsesArkiv.markForsendelseFailed( f, nesteforsok );
		Forsendelse f2 = forsendelsesArkiv.retrieve( f.getId() );
		assertTrue( "", f.getSendt().before( f2.getNesteForsok() ) );

		List<String> forsendelser = forsendelsesArkiv.readUnsent( new ShipmentPolicy[] { ShipmentPolicy.fromValue( f.getShipmentPolicy() ) } );
		assertFalse( forsendelser.contains( f2.getId() ) );

		forsendelsesArkiv.markForsendelseFailed( f, new Date( f.getSendt().getTime() - 1000L ) );

		List<String> forsendelser2 = forsendelsesArkiv.readUnsent( new ShipmentPolicy[] { ShipmentPolicy.fromValue( f.getShipmentPolicy() ) } );
		assertTrue( forsendelser2.contains( f2.getId() ) );
	}
}
