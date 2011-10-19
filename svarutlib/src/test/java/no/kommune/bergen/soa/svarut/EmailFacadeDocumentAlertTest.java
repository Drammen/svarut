package no.kommune.bergen.soa.svarut;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import no.kommune.bergen.soa.svarut.util.FilHenter;
import org.junit.Before;
import org.junit.Test;

public class EmailFacadeDocumentAlertTest {
	private static final String BodyTemplate = "BodyTemplate";
	private static final String PdfTemplate = "pdfTemplate";
	private static final String ReplyTo = "replyTo";
	private static final String SubjectTemplate = "subjectTemplate";
	private static final String ToTemplate = "toTemplate=%s";
	private TemplateEngine templateEngineMock;
	private MailSender mailSenderMock;
	private PdfGenerator pdfGeneratorMock;
	private EmailFacadeDocumentAlert emailFacade;
	private static final String fnr = ForsendelsesArkivTest.fnr;

	@Before
	public void init() {
		templateEngineMock = createStrictMock( TemplateEngine.class );
		mailSenderMock = createStrictMock( MailSender.class );
		pdfGeneratorMock = createStrictMock( PdfGenerator.class );
		emailFacade = new EmailFacadeDocumentAlert( templateEngineMock, mailSenderMock, VelocityModelFactoryTest.createVelocityModelFactory(), pdfGeneratorMock );
		emailFacade.setBodyTemplate( BodyTemplate );
		emailFacade.setPdfTemplate( PdfTemplate );
		emailFacade.setReplyTo( ReplyTo );
		emailFacade.setSubjectTemplate( SubjectTemplate );
		emailFacade.setToTemplate( ToTemplate );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void send() throws FileNotFoundException {
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		File file = FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf);
		forsendelse.setFile(file);
		forsendelse.setId(file.getName());
		expect( templateEngineMock.merge( isA( Map.class ), same( SubjectTemplate ) ) ).andReturn( SubjectTemplate );
		expect( templateEngineMock.merge(isA(Map.class), same(BodyTemplate)) ).andReturn( BodyTemplate );
		expect( pdfGeneratorMock.createPdf( isA( Map.class ), same( PdfTemplate ) ) ).andReturn(FilHenter.getFileAsInputStream("test.pdf"));
		mailSenderMock.sendEmail(contains(fnr), same(ReplyTo), same(SubjectTemplate), same(BodyTemplate), isA(File[].class));
		expectLastCall();
		replay( templateEngineMock );
		replay( pdfGeneratorMock );
		replay( mailSenderMock );
		emailFacade.send( forsendelse );
		verify( templateEngineMock );
		verify( pdfGeneratorMock );
		verify( mailSenderMock );
	}

}
