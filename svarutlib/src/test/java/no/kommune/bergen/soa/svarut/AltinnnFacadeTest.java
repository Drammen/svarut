package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.common.util.VelocityTemplateEngine;
import no.kommune.bergen.soa.svarut.AltinnFacade;
import no.kommune.bergen.soa.svarut.VelocityModelFactory;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceMessage;
import no.kommune.bergen.soa.svarut.altin.MockCorrespondenceClient;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AltinnnFacadeTest {
	private MockCorrespondenceClient myCorrespondenceClient;
	private AltinnFacade altinnFacade;

	@Before
	public void setup() {
		VelocityModelFactory velocityModelFactory = VelocityModelFactoryTest.createVelocityModelFactory();
		myCorrespondenceClient = new MockCorrespondenceClient( MockCorrespondenceClient.newSettings() );
		altinnFacade = new AltinnFacade( getTemplateEngine(), myCorrespondenceClient, velocityModelFactory );
	}

	@Test
	public void send() {
		Forsendelse forsendelse = newForsendelse();
		altinnFacade.send( forsendelse );
		CorrespondenceMessage message = myCorrespondenceClient.getLatestMessage();
		verifyMessageTitle( forsendelse, message );
		verifyMessageBody( forsendelse, message );
	}

	private void verifyMessageBody( Forsendelse forsendelse, CorrespondenceMessage message ) {
		Assert.assertFalse( message.getMessageBody().contains( "$" ) );
		Assert.assertTrue( message.getMessageBody().contains( forsendelse.getMeldingsTekst() ) );
		Assert.assertTrue( message.getMessageBody().contains( forsendelse.getId() ) );
	}

	private void verifyMessageTitle( Forsendelse forsendelse, CorrespondenceMessage message ) {
		Assert.assertFalse( message.getMessageTitle().contains( "$" ) );
		Assert.assertTrue( message.getMessageTitle().contains( forsendelse.getTittel() ) );
	}

	private TemplateEngine getTemplateEngine() {
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		VelocityEngine velocityEngine = (VelocityEngine)context.getBean( "velocityEngineFactoryBean" );
		VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
		velocityTemplateEngine.setVelocityEngine( velocityEngine );
		return velocityTemplateEngine;
	}

	private Forsendelse newForsendelse() {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		satisfyChecksInMockCorrespondenceClient( forsendelse );
		return forsendelse;
	}

	private void satisfyChecksInMockCorrespondenceClient( Forsendelse forsendelse ) {
		CorrespondenceMessage message = MockCorrespondenceClient.createMessage();
		forsendelse.setId( message.getExternalReference() );
		forsendelse.setMeldingsTekst( message.getMessageBody() );
		forsendelse.setTittel( message.getMessageTitle() );
	}
}
