package no.kommune.bergen.soa.svarut;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.Orgnr;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.util.Files;

import org.apache.commons.io.IOUtils;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//ID, FODSELSNR, NAVN, ADRESSE1, ADRESSE2, ADRESSE3, POSTNR, POSTSTED, LAND, TITTEL, MELDING, LEST, SENDT, NORGEDOTNO, UTSKREVET
public class ServiceDelegateImplTest {
	private ServiceContext serviceContext;
	private PrintFacade printFacadeMock;
	private ServiceDelegateImpl service;
	private boolean mailSenderCalled = false;
	private final MockMailSender mockMailSender = new MockMailSender();
	private static final String EmailTo = "svarut.to@qwe.com";
	private static final String EmailReplyTo = "svarut.from@qwe.com";

	private static final String Msg = "Deres søknad om fartsdempende tiltak i Absalon Beyersgate sendt 12.06.2008 12:46:23 er mottatt ved Samferdselsetaten og gitt saksnummer 200717218.\n" + "\n" + "Søknadens referansenummer er 1260853438309.\n"
			+ "\n" + "[ DETTE ER KUN EN TEST - Ref Per Ellingsen ]\n";

	@Before
	public void init() {
		new JdbcHelper().createTable( "FORSENDELSESARKIV" );
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		this.serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		this.serviceContext.verify();
		this.serviceContext.emailFacade.mailSender = mockMailSender;
		this.printFacadeMock = createStrictMock( PrintFacade.class );
		this.serviceContext.printFacade = this.printFacadeMock;
		this.serviceContext.getAltinnContext().setLeadTimeApost( -1L );
		this.serviceContext.verify();
		this.service = new ServiceDelegateImpl( this.serviceContext , new DispatchRateConfig(0,0,0));
		this.mailSenderCalled = false;
	}

	@Test
	public void propertiesLoad() throws Exception {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		String forsendesesId = service.send( forsendelse,  ForsendelsesArkivTest.getTestDocument() );
		String status = service.retrieveStatus( forsendesesId );
		assertTrue( status != null && status.length() > 0 );
		Properties properties = new Properties();
		properties.load( new ByteArrayInputStream( status.replace( ',', '\n' ).getBytes() ) );
		assertNotNull( properties.getProperty( "ID" ) );
		//System.out.println( "properties=" + properties.toString() );
	}

	@Test
	public void retrieveStatus2() throws Exception {
		String[] forsendesesIds = postSome( 1, 2 );
		List<Forsendelse> forsendelser = service.retrieveStatus( forsendesesIds );
		assertTrue( forsendelser != null && forsendelser.size() > 0 );
		for (Forsendelse f : forsendelser) {
			assertNotNull( f.getId() );
		}
	}

	@Test
	public void retrieveStatus3() throws Exception {
		List<String> forsendelsesIds = Arrays.asList( postSome( 1, 2 ) );
		long now = System.currentTimeMillis();
		final long TwentyFourHoursInMillis = 1000*60*60*24;
		Date yesterday = new Date( now - TwentyFourHoursInMillis );
		Date tomorrow = new Date( now + TwentyFourHoursInMillis );
		List<Forsendelse> forsendelser = service.retrieveStatus( yesterday, tomorrow );
		assertTrue( forsendelser != null && forsendelser.size() == 2 );
		for (Forsendelse f : forsendelser) {
			assertNotNull( f.getId() );
			assertTrue( forsendelsesIds.contains( f.getId() ) );
		}
	}

	private String[] postSome( int... is ) {
		List<String> forsendesesIds = new ArrayList<String>();
		for (int variant : is) {
			Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
			forsendesesIds.add( service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() ) );
		}
		return forsendesesIds.toArray( new String[forsendesesIds.size()] );
	}

	@Test
	public void sendAndRetrieveFnr() throws IOException {
		sendAndRetrieve( new Fodselsnr( ForsendelsesArkivTest.fnr ) );
	}

	@Test
	public void sendAndRetrieveOrg() throws IOException {
		sendAndRetrieve( new Orgnr( ForsendelsesArkivTest.orgnr ) );
	}

	private void sendAndRetrieve( JuridiskEnhet juridiskEnhet ) throws IOException {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		String forsendesesId = service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() );
		forsendelse = service.retrieve( forsendesesId, juridiskEnhet );
		InputStream inputStream = service.retrieveContent( forsendesesId, juridiskEnhet );
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.stream( inputStream, byteArrayOutputStream );
		byte[] retrievedContent = byteArrayOutputStream.toByteArray();
		assertEquals( new String(IOUtils.toByteArray(ForsendelsesArkivTest.getTestDocument())), new String(retrievedContent) );
		ForsendelsesArkivTest.validateForsendelse( forsendelse, variant );
	}

	@Test
	public void saveThenRetrieveSeveralFnr() {
		saveThenRetrieveSeveral( new Fodselsnr( ForsendelsesArkivTest.fnr ) );
	}

	@Test
	public void saveThenRetrieveSeveralOrg() {
		saveThenRetrieveSeveral( new Orgnr( ForsendelsesArkivTest.orgnr ) );
	}

	private void saveThenRetrieveSeveral( JuridiskEnhet juridiskEnhet ) {
		final int count = 4;
		for (int variant = 1; variant <= count; variant++) {
			Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
			String forsendesesId = service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() );
			assertNotNull( forsendesesId );
		}
		List<Forsendelse> forsendelser = service.retrieveList( juridiskEnhet );
		assertEquals( count, forsendelser.size() );
		for (Forsendelse f : forsendelser) {
			ForsendelsesArkivTest.validateForsendelse( f, ForsendelsesArkivTest.getVariant( f ) );
		}
	}

	/** Tester at forsendelser merket kun email ikke sendes til Altinn */
	@Test
	public void sendEmail() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setShipmentPolicy( ShipmentPolicy.KUN_EMAIL.value());
		forsendelse.setEmail( EmailTo );
		forsendelse.setReplyTo( EmailReplyTo );
		String fnr = forsendelse.getFnr();
		forsendelse.setMeldingsTekst( Msg );
		String forsendesesId = service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() );
		String statusBefore = service.retrieveStatus( forsendesesId );
		service.dispatch();
		String status = service.retrieveStatus( forsendesesId );
		assertFalse( status.indexOf( "EPOST_SENDT=null" ) > -1 );
		assertTrue( status.indexOf( "LEST=null" ) > -1 );
		assertTrue( status.indexOf( "ALTINN_SENDT=null" ) > -1 );
		assertTrue( status.indexOf( "UTSKREVET=null" ) > -1 );
		assertFalse( status.indexOf( "STOPPET=null" ) > -1 );
		assertTrue( status.indexOf( "FORSENDELSES_MATE=KunEmail" ) > -1 );
		assertTrue( status.indexOf( "FODSELSNR=" + fnr ) > -1 );
		assertTrue( status.indexOf( Msg ) > -1 );
		assertTrue( this.mailSenderCalled );
		assertFalse( status.equals( statusBefore ) );
	}

	/** Tester at forsendelser merket kun email (uten vedlegg) ikke sendes til Altinn */
	@Test
	public void sendEmailNoAttachment() {
		mockMailSender.includeAttachent = false;
		mockMailSender.emailTo = EmailTo;
		mockMailSender.emailReplyTo = EmailReplyTo;
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setShipmentPolicy( ShipmentPolicy.KUN_EMAIL_INGEN_VEDLEGG.value());
		forsendelse.setEmail( EmailTo );
		forsendelse.setReplyTo( EmailReplyTo );
		String fnr = forsendelse.getFnr();
		forsendelse.setMeldingsTekst( Msg );
		String forsendesesId = service.send( forsendelse, new ByteArrayInputStream( Msg.getBytes() ) );
		String statusBefore = service.retrieveStatus( forsendesesId );
		service.dispatch();
		String status = service.retrieveStatus( forsendesesId );
		assertFalse( status.indexOf( "EPOST_SENDT=null" ) > -1 );
		assertTrue( status.indexOf( "LEST=null" ) > -1 );
		assertTrue( status.indexOf( "ALTINN_SENDT=null" ) > -1 );
		assertTrue( status.indexOf( "UTSKREVET=null" ) > -1 );
		assertFalse( status.indexOf( "STOPPET=null" ) > -1 );
		assertTrue( status.indexOf( "FORSENDELSES_MATE=KunEmail" ) > -1 );
		assertTrue( status.indexOf( "FODSELSNR=" + fnr ) > -1 );
		assertTrue( status.indexOf( Msg ) > -1 );
		assertTrue( this.mailSenderCalled );
		assertFalse( status.equals( statusBefore ) );
	}

	/** Tester at forsendelser merket kun A-post ikke sendes til Altinn */
	@Test
	public void sendPrint() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setShipmentPolicy( ShipmentPolicy.KUN_APOST.value());
		String forsendesesId = service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() );
		expect( printFacadeMock.print( isA( Forsendelse.class ) ) ).andReturn( newPrintReceipt( forsendesesId ) );
		replay( printFacadeMock );
		service.dispatch();
		verify( printFacadeMock );
		String status = service.retrieveStatus( forsendesesId );
		assertTrue( status.indexOf( "ALTINN_SENDT=null" ) > -1 );
		assertFalse( status.indexOf( "UTSKREVET=null" ) > -1 );
		assertFalse( this.mailSenderCalled );
	}

	@Test
	public void importPrintStatements() {
		this.printFacadeMock.importPrintStatements( EasyMock.eq( service.serviceContext.forsendelsesArkiv ) );
		EasyMock.replay( printFacadeMock );
		this.service.importPrintStatements();
		EasyMock.verify( printFacadeMock );
	}

	@Test
	public void confirm() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		String forsendesesId = service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() );
		String status = service.retrieveStatus( forsendesesId );
		assertTrue( status.indexOf( "SENDT=" ) > -1 );
		assertTrue( status.indexOf( "LEST=" ) > -1 );
		assertFalse( status.indexOf( " SENDT=null" ) > -1 );
		assertTrue( status.indexOf( "LEST=null" ) > -1 );
		service.confirm( forsendesesId );
		status = service.retrieveStatus( forsendesesId );
		assertFalse( status.indexOf( "LEST=null" ) > -1 );
		service.setUnread( forsendesesId );
		status = service.retrieveStatus( forsendesesId );
		assertTrue( status.indexOf( "LEST=null" ) > -1 );
	}

	@Test
	public void getUrlFnr() throws MalformedURLException {
		getUrl( new Fodselsnr( ForsendelsesArkivTest.fnr ) );
	}

	@Test
	public void getUrlOrg() throws MalformedURLException {
		getUrl( new Orgnr( ForsendelsesArkivTest.orgnr ) );
	}

	private void getUrl( JuridiskEnhet juridiskEnhet ) throws MalformedURLException {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		String forsendesesId = service.send( forsendelse, ForsendelsesArkivTest.getTestDocument() );
		String url = service.getUrl( juridiskEnhet, forsendesesId );
		assertNotNull( url );
		assertTrue( url.length() > 0 );
		assertNotNull( new URL( url ) );
		assertTrue( url.contains( juridiskEnhet.getValue() ) );
	}

	@Test
	public void removeOld() {
		int count = 4;
		String[] ids = new String[count];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = service.send( ForsendelsesArkivTest.createForsendelse( i ), ForsendelsesArkivTest.getTestDocument() );
		}
		assertTrue( service.statistics().indexOf( "SENDT=" + count ) > -1 );
		service.setRetirementAgeIndays( -1 );
		service.removeOld();
		assertTrue( service.statistics().indexOf( "SENDT=" + count ) > -1 );
		for (String id : ids) {
			service.confirm( id );
		}
		service.removeOld();
		assertTrue( service.statistics().indexOf( "SENDT=0" ) > -1 );
	}

	@Test
	public void printUnread() {
		int count = 4;
		String[] ids = new String[count];
		for (int i = 0; i < ids.length; i++) {
			Forsendelse f = ForsendelsesArkivTest.createForsendelse( i );
			ids[i] = service.send( f, ForsendelsesArkivTest.getTestDocument() );
			expect( printFacadeMock.print( isA( Forsendelse.class ) ) ).andReturn( newPrintReceipt( ids[i] ) );
		}
		assertTrue( service.statistics().indexOf( "UTSKREVET=0" ) > -1 );
		assertTrue( service.statistics().indexOf( "SENDT=" + count ) > -1 );
		replay( printFacadeMock );
		service.printUnread();
		verify( printFacadeMock );
		assertTrue( service.statistics().indexOf( "UTSKREVET=" + count ) > -1 );
		assertTrue( service.statistics().indexOf( "STOPPET=" + count ) > -1 );
	}

	private PrintReceipt newPrintReceipt( String printId ) {
		PrintReceipt printReceipt = new PrintReceipt();
		printReceipt.setPrintId( printId );
		return printReceipt;
	}

	class MockMailSender extends MailSender {
		public boolean includeAttachent = true;
		public String emailTo = null;
		public String emailReplyTo = null;

		@Override
		public void sendEmail( final String to, final String from, final String subject, final String body, final File[] attachments ) {
			ServiceDelegateImplTest.this.mailSenderCalled = true;
			assertNotNull( to );
			assertTrue( to.length() > 0 );
			assertTrue( to.indexOf( '@' ) > -1 );
			assertNotNull( from );
			assertTrue( from.length() > 0 );
			assertTrue( from.indexOf( '@' ) > -1 );
			assertNotNull( subject );
			assertTrue( subject.length() > 0 );
			assertTrue( subject.indexOf( '$' ) == -1 );
			assertNotNull( body );
			assertTrue( body.length() > 0 );
			assertTrue( body.indexOf( '$' ) == -1 );
			if (includeAttachent) {
				assertNotNull( attachments );
				assertTrue( attachments.length > 0 );
				assertNotNull( attachments[0] );
			} else {
				assertNull( attachments );
			}
			if (this.emailTo != null) assertEquals( this.emailTo, to );
			if (this.emailReplyTo != null) assertEquals( this.emailReplyTo, from );
			//PrintFacadeTest.view( attachments[0] );
		}

	}

}
