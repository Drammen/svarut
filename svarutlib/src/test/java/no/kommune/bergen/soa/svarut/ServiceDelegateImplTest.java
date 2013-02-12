package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.*;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.util.Files;
import org.apache.commons.io.IOUtils;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

//ID, FODSELSNR, NAVN, ADRESSE1, ADRESSE2, ADRESSE3, POSTNR, POSTSTED, LAND, TITTEL, MELDING, LEST, SENDT, NORGEDOTNO, UTSKREVET
public class ServiceDelegateImplTest {

	private static final Logger log = LoggerFactory.getLogger(ServiceDelegateImplTest.class);

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
		new JdbcHelper().createTable("FORSENDELSESARKIV");
		System.setProperty("CONSTRETTO_TAGS", "DEV");
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ServiceContext serviceContext = (ServiceContext) context.getBean("serviceContext");
		serviceContext.verify();
		serviceContext.emailFacade.mailSender = mockMailSender;
		printFacadeMock = createStrictMock(PrintFacade.class);
		serviceContext.printFacade = printFacadeMock;
		serviceContext.getAltinnContext().setLeadTimeApost(-1L);
		serviceContext.verify();
		service = new ServiceDelegateImpl(serviceContext, new DispatchRateConfig(0, 0, 0));
		mailSenderCalled = false;
	}

	@Test
	public void propertiesLoad() throws Exception {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
		String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
		String status = service.retrieveStatus(forsendelsesId);
		assertTrue(status != null && status.length() > 0);
		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(status.replace(',', '\n').getBytes()));
		assertNotNull(properties.getProperty("ID"));
		//System.out.println( "properties=" + properties.toString() );
	}

	@Test
	public void retrieveStatus2() throws Exception {
		String[] forsendelsesIds = postSome(1, 2);
		List<Forsendelse> forsendelser = service.retrieveStatus(forsendelsesIds);
		assertTrue(forsendelser != null && forsendelser.size() > 0);
		for (Forsendelse f : forsendelser) {
			assertNotNull(f.getId());
		}
	}

	@Test
	public void retrieveStatus3() throws Exception {
		List<String> forsendelsesIds = Arrays.asList(postSome(1, 2));
		long now = System.currentTimeMillis();
		final long TwentyFourHoursInMillis = 1000 * 60 * 60 * 24;
		Date yesterday = new Date(now - TwentyFourHoursInMillis);
		Date tomorrow = new Date(now + TwentyFourHoursInMillis);
		List<Forsendelse> forsendelser = service.retrieveStatus(yesterday, tomorrow);
		assertTrue(forsendelser != null && forsendelser.size() == 2);
		for (Forsendelse f : forsendelser) {
			assertNotNull(f.getId());
			assertTrue(forsendelsesIds.contains(f.getId()));
		}
	}

	private String[] postSome(int... is) {
		List<String> forsendelsesIds = new ArrayList<String>();
		for (int variant : is) {
			Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
			forsendelsesIds.add(service.send(forsendelse, ForsendelsesArkivTest.getTestDocument()));
		}
		return forsendelsesIds.toArray(new String[forsendelsesIds.size()]);
	}

	@Test
	public void sendAndRetrieveFnr() throws IOException {
		sendAndRetrieve(new Fodselsnr(ForsendelsesArkivTest.fnr));
	}

	@Test
	public void sendAndRetrieveOrg() throws IOException {
		sendAndRetrieve(new Orgnr(ForsendelsesArkivTest.orgnr));
	}

	private void sendAndRetrieve(JuridiskEnhet juridiskEnhet) throws IOException {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
		String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
		forsendelse = service.retrieve(forsendelsesId, juridiskEnhet);
		InputStream inputStream = service.retrieveContent(forsendelsesId, juridiskEnhet);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.stream(inputStream, byteArrayOutputStream);
		byte[] retrievedContent = byteArrayOutputStream.toByteArray();
		assertEquals(new String(IOUtils.toByteArray(ForsendelsesArkivTest.getTestDocument())), new String(retrievedContent));
		ForsendelsesArkivTest.validateForsendelse(forsendelse, variant);
	}

	@Test
	public void saveThenRetrieveSeveralFnr() {
		saveThenRetrieveSeveral(new Fodselsnr(ForsendelsesArkivTest.fnr));
	}

	@Test
	public void saveThenRetrieveSeveralOrg() {
		saveThenRetrieveSeveral(new Orgnr(ForsendelsesArkivTest.orgnr));
	}

	private void saveThenRetrieveSeveral(JuridiskEnhet juridiskEnhet) {
		final int count = 4;
		for (int variant = 1; variant <= count; variant++) {
			Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
			String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
			assertNotNull(forsendelsesId);
		}
		List<Forsendelse> forsendelser = service.retrieveList(juridiskEnhet);
		assertEquals(count, forsendelser.size());
		for (Forsendelse f : forsendelser) {
			ForsendelsesArkivTest.validateForsendelse(f, ForsendelsesArkivTest.getVariant(f));
		}
	}

	/**
	 * Tester at forsendelser merket kun email ikke sendes til Altinn
	 */
	@Test
	public void sendEmail() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
		forsendelse.setShipmentPolicy(ShipmentPolicy.KUN_EMAIL.value());
		forsendelse.setEmail(EmailTo);
		forsendelse.setReplyTo(EmailReplyTo);
		String fnr = forsendelse.getFnr();
		forsendelse.setMeldingsTekst(Msg);
		String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
		String statusBefore = service.retrieveStatus(forsendelsesId);
		service.dispatch();
		String status = service.retrieveStatus(forsendelsesId);
		assertFalse(status.contains("EPOST_SENDT=null"));
		assertTrue(status.contains("LEST=null"));
		assertTrue(status.contains("ALTINN_SENDT=null"));
		assertTrue(status.contains("UTSKREVET=null"));
		assertFalse(status.contains("STOPPET=null"));
		assertTrue(status.contains("FORSENDELSES_MATE=KunEmail"));
		assertTrue(status.contains("FODSELSNR=" + fnr));
		assertTrue(status.contains(Msg));
		assertTrue(mailSenderCalled);
		assertFalse(status.equals(statusBefore));
	}

	/**
	 * Tester at forsendelser merket kun email (uten vedlegg) ikke sendes til Altinn
	 */
	@Test
	public void sendEmailNoAttachment() {
		mockMailSender.includeAttachment = false;
		mockMailSender.emailTo = EmailTo;
		mockMailSender.emailReplyTo = EmailReplyTo;
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
		forsendelse.setShipmentPolicy(ShipmentPolicy.KUN_EMAIL_INGEN_VEDLEGG.value());
		forsendelse.setEmail(EmailTo);
		forsendelse.setReplyTo(EmailReplyTo);
		String fnr = forsendelse.getFnr();
		forsendelse.setMeldingsTekst(Msg);
		String forsendelsesId = service.send(forsendelse, new ByteArrayInputStream(Msg.getBytes()));
		String statusBefore = service.retrieveStatus(forsendelsesId);
		service.dispatch();
		String status = service.retrieveStatus(forsendelsesId);
		assertFalse(status.contains("EPOST_SENDT=null"));
		assertTrue(status.contains("LEST=null"));
		assertTrue(status.contains("ALTINN_SENDT=null"));
		assertTrue(status.contains("UTSKREVET=null"));
		assertFalse(status.contains("STOPPET=null"));
		assertTrue(status.contains("FORSENDELSES_MATE=KunEmail"));
		assertTrue(status.contains("FODSELSNR=" + fnr));
		assertTrue(status.contains(Msg));
		assertTrue(mailSenderCalled);
		assertFalse(status.equals(statusBefore));
	}

	/**
	 * Tester at forsendelser merket kun A-post ikke sendes til Altinn
	 */
	@Test
	public void sendPrint() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
		forsendelse.setShipmentPolicy(ShipmentPolicy.KUN_APOST.value());
		String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
		expect(printFacadeMock.print(isA(Forsendelse.class))).andReturn(newPrintReceipt(forsendelsesId));
		replay(printFacadeMock);
		service.dispatch();
		verify(printFacadeMock);
		String status = service.retrieveStatus(forsendelsesId);
		assertTrue(status.contains("ALTINN_SENDT=null"));
		assertFalse(status.contains("UTSKREVET=null"));
		assertFalse(mailSenderCalled);
	}

	@Test
	public void importPrintStatements() {
		printFacadeMock.importPrintStatements(EasyMock.eq(service.serviceContext.forsendelsesArkiv));
		EasyMock.replay(printFacadeMock);
		service.importPrintStatements();
		EasyMock.verify(printFacadeMock);
	}

	@Test
	public void confirm() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(variant);
		String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
		String status = service.retrieveStatus(forsendelsesId);
		assertTrue(status.contains("SENDT="));
		assertTrue(status.contains("LEST="));
		assertFalse(status.contains(" SENDT=null"));
		assertTrue(status.contains("LEST=null"));
		service.confirm(forsendelsesId);
		status = service.retrieveStatus(forsendelsesId);
		assertFalse(status.contains("LEST=null"));
		service.setUnread(forsendelsesId);
		status = service.retrieveStatus(forsendelsesId);
		assertTrue(status.contains("LEST=null"));
	}

	@Test
	public void getUrlFnr() throws MalformedURLException {
		getUrl(new Fodselsnr(ForsendelsesArkivTest.fnr));
	}

	@Test
	public void getUrlOrg() throws MalformedURLException {
		getUrl(new Orgnr(ForsendelsesArkivTest.orgnr));
	}

	private void getUrl(JuridiskEnhet juridiskEnhet) throws MalformedURLException {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse(1);
		String forsendelsesId = service.send(forsendelse, ForsendelsesArkivTest.getTestDocument());
		String url = service.getUrl(juridiskEnhet, forsendelsesId);
		assertNotNull(url);
		assertTrue(url.length() > 0);
		assertNotNull(new URL(url));
		assertTrue(url.contains(juridiskEnhet.getValue()));
	}

	@Test
	public void removeOld() {
		int count = 4;
		String[] ids = new String[count];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = service.send(ForsendelsesArkivTest.createForsendelse(i), ForsendelsesArkivTest.getTestDocument());
		}
		assertTrue(service.statistics().contains("SENDT=" + count));
		service.setRetirementAgeInDays(-1);
		service.removeOld();
		assertTrue(service.statistics().contains("SENDT=" + count));
		for (String id : ids) {
			service.confirm(id);
		}
		service.removeOld();
		assertTrue(service.statistics().contains("SENDT=0"));
	}

	@Test
	public void printUnread() {
		int count = 4;
		String[] ids = new String[count];
		for (int i = 0; i < ids.length; i++) {
			Forsendelse f = ForsendelsesArkivTest.createForsendelse(i);
			ids[i] = service.send(f, ForsendelsesArkivTest.getTestDocument());
			expect(printFacadeMock.print(isA(Forsendelse.class))).andReturn(newPrintReceipt(ids[i]));
		}
		assertTrue(service.statistics().contains("UTSKREVET=0"));
		assertTrue(service.statistics().contains("SENDT=" + count));
		replay(printFacadeMock);
		service.printUnread();
		verify(printFacadeMock);
		assertTrue(service.statistics().contains("UTSKREVET=" + count));
		assertTrue(service.statistics().contains("STOPPET=" + count));
	}

	private PrintReceipt newPrintReceipt(String printId) {
		PrintReceipt printReceipt = new PrintReceipt();
		printReceipt.setPrintId(printId);
		return printReceipt;
	}

	class MockMailSender extends MailSender {
		public boolean includeAttachment = true;
		public String emailTo = null;
		public String emailReplyTo = null;

		@Override
		public void sendEmail(final String to, final String from, final String subject, final String body, final File[] attachments) {
			mailSenderCalled = true;
			assertNotNull(to);
			assertTrue(to.length() > 0);
			assertTrue(to.indexOf('@') > -1);
			assertNotNull(from);
			assertTrue(from.length() > 0);
			assertTrue(from.indexOf('@') > -1);
			assertNotNull(subject);
			assertTrue(subject.length() > 0);
			assertTrue(subject.indexOf('$') == -1);
			assertNotNull(body);
			assertTrue(body.length() > 0);
			assertTrue(body.indexOf('$') == -1);
			if (includeAttachment) {
				assertNotNull(attachments);
				assertTrue(attachments.length > 0);
				assertNotNull(attachments[0]);
			} else {
				assertNull(attachments);
			}
			if (emailTo != null) assertEquals(emailTo, to);
			if (emailReplyTo != null) assertEquals(emailReplyTo, from);
			//PrintFacadeTest.view( attachments[0] );

			log.info("Subject=" + subject);
			log.info("Body=" + body);
		}
	}
}
