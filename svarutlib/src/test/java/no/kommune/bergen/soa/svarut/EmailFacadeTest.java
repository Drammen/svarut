package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.svarut.util.FilHenter;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

public class EmailFacadeTest {
	private static final String BodyTemplate = "bodyTemplate", BodyTemplateNoAttachment = "bodyTemplateNoAttachment", ReplyTo = "replyTo", SubjectTemplate = "subjectTemplate", ToTemplate = "toTemplate=%s", Fnr = ForsendelsesArkivTest.fnr;
	private TemplateEngine templateEngineMock;
	private MailSender mailSenderMock;
	private EmailFacade emailFacade;

	@Before
	public void init() {
		templateEngineMock = createStrictMock( TemplateEngine.class );
		mailSenderMock = createStrictMock( MailSender.class );
		emailFacade = new EmailFacade( templateEngineMock, mailSenderMock, VelocityModelFactoryTest.createVelocityModelFactory() );
		emailFacade.setBodyTemplate( BodyTemplate );
		emailFacade.setBodyTemplateNoAttachment( BodyTemplateNoAttachment );
		emailFacade.setReplyTo( ReplyTo );
		emailFacade.setSubjectTemplate( SubjectTemplate );
		emailFacade.setToTemplate( ToTemplate );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void sendIncludeAttachment() {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		File file = FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf);
		forsendelse.setFile( file );
		forsendelse.setId( file.getName() );
		expect( templateEngineMock.merge( isA( Map.class ), same( SubjectTemplate ) ) ).andReturn( SubjectTemplate );
		expect( templateEngineMock.merge( isA( Map.class ), same( BodyTemplate ) ) ).andReturn( BodyTemplate );
		mailSenderMock.sendEmail( contains( Fnr ), same( ReplyTo ), same( SubjectTemplate ), same( BodyTemplate ), isA( File[].class ) );
		expectLastCall();
		replay( templateEngineMock );
		replay( mailSenderMock );
		emailFacade.send( forsendelse );
		verify( templateEngineMock );
		verify( mailSenderMock );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void sendNoAttachment() {
		String email = "svarut.to@hudson.iktfou.no";
		String replyTo = "svarut.from@hudson.iktfou.no";
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		forsendelse.setId( "sendNoAttachment" );
		forsendelse.setEmail( email );
		forsendelse.setShipmentPolicy( ShipmentPolicy.KUN_EMAIL.value() );
		forsendelse.setReplyTo( replyTo );
		expect( templateEngineMock.merge( isA( Map.class ), same( SubjectTemplate ) ) ).andReturn( SubjectTemplate );
		expect( templateEngineMock.merge( isA( Map.class ), same( BodyTemplateNoAttachment ) ) ).andReturn( BodyTemplateNoAttachment );
		mailSenderMock.sendEmail( same( email ), same( replyTo ), same( SubjectTemplate ), same( BodyTemplateNoAttachment ), (File[])EasyMock.isNull() );
		expectLastCall();
		replay( templateEngineMock );
		replay( mailSenderMock );
		emailFacade.send( forsendelse );
		verify( templateEngineMock );
		verify( mailSenderMock );
	}

	@Test
	public void creteAttachments() {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		forsendelse.setId( "MyID" );
		Map<String, String> model = this.emailFacade.modelFactory.createModel( forsendelse );
		File[] attachments = this.emailFacade.createAttachments( forsendelse, model );
		Assert.assertNull( attachments );
		File file = FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf);
		forsendelse.setFile( file );
		attachments = this.emailFacade.createAttachments( forsendelse, model );
		Assert.assertNotNull( attachments );
		Assert.assertEquals( 1, attachments.length );
	}
}
