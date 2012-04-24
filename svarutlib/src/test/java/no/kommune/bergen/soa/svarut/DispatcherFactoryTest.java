package no.kommune.bergen.soa.svarut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.dispatchers.AltinnOgPost;
import no.kommune.bergen.soa.svarut.dispatchers.EmailOgPost;
import no.kommune.bergen.soa.svarut.dispatchers.KunAltinn;
import no.kommune.bergen.soa.svarut.dispatchers.KunEmail;
import no.kommune.bergen.soa.svarut.dispatchers.KunPost;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DispatcherFactoryTest {
	DispatcherFactory dispatcherFactory;

    @Before
	public void init() {
		new JdbcHelper().createTable( "FORSENDELSESARKIV" );
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		ServiceContext serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		serviceContext.verify();
		ServiceDelegate serviceDelegate = EasyMock.createStrictMock(ServiceDelegate.class);
		dispatcherFactory = new DispatcherFactory( serviceDelegate,serviceContext , new DispatchRateConfig(0,0,0));
		serviceContext.verify();
	}

	@Test
	public void knownDispatchers() {

        List<Dispatcher> allDispatchers = dispatcherFactory.getAllDispatchers();
        assertEquals("Antall dispatchere",5,allDispatchers.size());
	}

	@Test
	public void getDispatcher() {
		int variant = 1;
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( variant );
		forsendelse.setEmail( "svarut.to@qwe.com" );

		verifyDispatcherChoice(forsendelse, ShipmentPolicy.KUN_APOST);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.KUN_BPOST);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.KUN_REKOMMANDERT);

		verifyDispatcherChoice(forsendelse, ShipmentPolicy.KUN_ALTINN);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.ALTINN_OG_APOST);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.ALTINN_OG_BPOST);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.ALTINN_OG_REKOMMANDERT);

		verifyDispatcherChoice(forsendelse, ShipmentPolicy.KUN_EMAIL);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.KUN_EMAIL_INGEN_VEDLEGG);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.EMAIL_OG_APOST);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.EMAIL_OG_BPOST);
		verifyDispatcherChoice(forsendelse, ShipmentPolicy.EMAIL_OG_REKOMMANDERT);
	}

	private void verifyDispatcherChoice(Forsendelse forsendelse, ShipmentPolicy shipmentPolicy) {
		forsendelse.setShipmentPolicy( shipmentPolicy.value() );
		Dispatcher dispatcher = dispatcherFactory.getDispatcher( forsendelse );
		assertNotNull( "dispatcher " + shipmentPolicy + " is null", dispatcher );

		if (ShipmentPolicy.KUN_APOST.equals( shipmentPolicy )) assertTrue( dispatcher instanceof KunPost );
		if (ShipmentPolicy.KUN_BPOST.equals( shipmentPolicy )) assertTrue( dispatcher instanceof KunPost );
		if (ShipmentPolicy.KUN_REKOMMANDERT.equals( shipmentPolicy )) assertTrue( dispatcher instanceof KunPost );

		if (ShipmentPolicy.KUN_ALTINN.equals( shipmentPolicy )) assertTrue( dispatcher instanceof KunAltinn );
		if (ShipmentPolicy.ALTINN_OG_APOST.equals( shipmentPolicy )) assertTrue( dispatcher instanceof AltinnOgPost );
		if (ShipmentPolicy.ALTINN_OG_BPOST.equals( shipmentPolicy )) assertTrue( dispatcher instanceof AltinnOgPost );
		if (ShipmentPolicy.ALTINN_OG_REKOMMANDERT.equals( shipmentPolicy )) assertTrue( dispatcher instanceof AltinnOgPost );

		if (ShipmentPolicy.KUN_EMAIL.equals( shipmentPolicy )) assertTrue( dispatcher instanceof KunEmail );
		if (ShipmentPolicy.KUN_EMAIL_INGEN_VEDLEGG.equals( shipmentPolicy )) {
			assertTrue( dispatcher instanceof KunEmail );
			assertTrue( ((KunEmail)dispatcher).isSkipAttachments(forsendelse) );
		}
		if (ShipmentPolicy.EMAIL_OG_APOST.equals( shipmentPolicy )) assertTrue( dispatcher instanceof EmailOgPost );
		if (ShipmentPolicy.EMAIL_OG_BPOST.equals( shipmentPolicy )) assertTrue( dispatcher instanceof EmailOgPost );
		if (ShipmentPolicy.EMAIL_OG_REKOMMANDERT.equals( shipmentPolicy )) assertTrue( dispatcher instanceof EmailOgPost );
	}

}
