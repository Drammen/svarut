package no.kommune.bergen.soa.svarut;

import java.io.File;

import no.kommune.bergen.soa.common.pdf.PdfGeneratorImpl;
import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.VelocityTemplateEngine;
import no.kommune.bergen.soa.svarut.EmailFacade;
import no.kommune.bergen.soa.svarut.EmailFacadeDocumentAlert;
import no.kommune.bergen.soa.svarut.ServiceContext;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import no.kommune.bergen.soa.svarut.util.FilHenter;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailFacadeIntTest {
	private final static String fnr = "24035738572";

	@Test
	@Ignore
	public void sendToHudson() {
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		VelocityEngine velocityEngine = (VelocityEngine)context.getBean( "velocityEngineFactoryBean" );
		VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
		velocityTemplateEngine.setVelocityEngine( velocityEngine );
		JavaMailSenderImpl javaMailSender = (JavaMailSenderImpl)context.getBean( "javaMailSenderImplHudson" );
		MailSender mailSender = new MailSender();
		mailSender.setJavaMailSender( javaMailSender );
		EmailFacadeDocumentAlert emailFacade = new EmailFacadeDocumentAlert( velocityTemplateEngine, mailSender,
				VelocityModelFactoryTest.createVelocityModelFactory(), new PdfGeneratorImpl( "target" ) );
		emailFacade.setBodyTemplate( "$TITTEL" );
		emailFacade.setPdfTemplate( "{TITTEL}\n\n{MELDING}\n\n{Link} {Help-Link} {ReaderDownload-Link}\n" );
		emailFacade.setReplyTo( "einarvalen@yahoo.com" );
		emailFacade.setSubjectTemplate( "$MELDING" );
		emailFacade.setToTemplate( "%s@innbygger.norge.no" );
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		File file = FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf);
		forsendelse.setFile( file );
		forsendelse.setId( file.getName() );
		emailFacade.send( forsendelse );
	}

	@Test
	@Ignore
	public void sendToMinSideNorgeDotNoDEV() {
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		sendToMinSideNorgeDotNo();
	}

	@Test
	@Ignore
	public void sendToMinSideNorgeDotNoATEST() {
		System.setProperty( "CONSTRETTO_TAGS", "ATEST" );
		sendToMinSideNorgeDotNo();
	}

	private void sendToMinSideNorgeDotNo() {
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		ServiceContext serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		EmailFacade emailFacade = serviceContext.getEmailFacadeNorgeDotNoDocumentAlert();
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		forsendelse.setFile( FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf) );
		forsendelse.setFnr( fnr );
		forsendelse.setTittel( "Tester alert" );
		forsendelse.setId( FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf).getName() );
		emailFacade.send( forsendelse );
	}
}
