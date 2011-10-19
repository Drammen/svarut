package no.kommune.bergen.soa.svarut;

import java.lang.reflect.Field;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.common.util.VelocityTemplateEngine;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.context.AltinnContext;
import no.kommune.bergen.soa.svarut.context.ArchiveContext;
import no.kommune.bergen.soa.svarut.context.DownloadContext;
import no.kommune.bergen.soa.svarut.context.EmailContext;
import no.kommune.bergen.soa.svarut.context.MessageTemplateAssembly;
import no.kommune.bergen.soa.svarut.context.NorgeDotNoContext;
import no.kommune.bergen.soa.svarut.context.PrintContext;
import no.kommune.bergen.soa.svarut.dao.FileStore;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/** Holding tank for context */
public class ServiceContext {
	static final Logger logger = Logger.getLogger( ServiceContext.class );
	final ForsendelsesArkiv forsendelsesArkiv;
	LdapFacade ldapFacade;
	final EmailFacade emailFacadeNorgeDotNoDocumentAlert, emailFacadeNorgeDotNoDocumentAttached, emailFacade;
	PrintFacade printFacade;
	final PdfGenerator pdfGenerator;
	final VelocityModelFactory velocityModelFactory;
	private final AltinnFacade altinnFacade;
	private final TemplateEngine templateEngine;
	private final VelocityEngine velocityEngine;

	private final NorgeDotNoContext norgeDotNoContext;
	private final AltinnContext altinnContext;
	private final EmailContext emailContext;
	private final PrintContext printContext;
	private final DownloadContext downloadContext;
	private final ArchiveContext archiveContext;

    public ServiceContext( VelocityEngine velocityEngine,
                           NorgeDotNoContext norgeDotNoContext,
                           AltinnContext altinnContext,
                           EmailContext emailContext,
                           PrintContext printContext,
                           DownloadContext downloadContext,
                           ArchiveContext archiveContext) {
		this.velocityEngine = velocityEngine;
		this.norgeDotNoContext = norgeDotNoContext;
		this.altinnContext = altinnContext;
		this.emailContext = emailContext;
		this.printContext = printContext;
		this.downloadContext = downloadContext;
		this.archiveContext = archiveContext;
        verifyParams();
		this.templateEngine = createTemplateEngine();
		this.velocityModelFactory = createVelocityModelFactory();
		this.pdfGenerator = new SvarUtPdfGenerator( this.archiveContext.getTempDir() );
		this.forsendelsesArkiv = createForsendelsesArkiv();
		this.ldapFacade = createLdapFacade( this.norgeDotNoContext.getLdapContextSource(), this.norgeDotNoContext.getLdapFilterTemplate() );
		this.emailFacadeNorgeDotNoDocumentAttached = createEmailFacadeDocumentAttached( this.norgeDotNoContext.getJavaMailSender(), this.norgeDotNoContext.getMessageTemplateAssembly() );
		this.emailFacadeNorgeDotNoDocumentAlert = createEmailFacadeDocumentAlert( this.norgeDotNoContext.getJavaMailSender(), this.norgeDotNoContext.getMessageTemplateAssembly() );
		this.emailFacade = createEmailFacadeDocumentAttached( this.emailContext.getJavaMailSender(), this.emailContext.getMessageTemplateAssembly() );
		this.altinnFacade = createAltinnFacade( this.altinnContext.getCorrespondenceSettings() );
		this.printFacade = createPrintFacade( this.printContext.getFrontPageTemplate(), this.printContext.getPrintServiceProvider() );
		verify();
	}

	private void verifyParams() {
		this.norgeDotNoContext.verify();
		this.altinnContext.verify();
		this.emailContext.verify();
		this.printContext.verify();
		this.downloadContext.verify();
		this.archiveContext.verify();
	}

	private PrintFacade createPrintFacade( String frontPageTemplate, PrintServiceProvider printServiceProvider ) {
		return new PrintFacade( pdfGenerator, frontPageTemplate, printServiceProvider, this.getVelocityModelFactory() );
	}

	private VelocityModelFactory createVelocityModelFactory() {
		return new VelocityModelFactory( this.downloadContext.getServletPathTemplate(), this.downloadContext.getPdfLinkText(), this.downloadContext.getHelpLink(), this.downloadContext.getHelpLinkText(), this.downloadContext.getReaderDownloadLink(),
				this.downloadContext.getReaderDownloadLinkText() );
	}

	private AltinnFacade createAltinnFacade( CorrespondenceSettings correspondenceSettings ) {
		CorrespondenceClient correspondenceClient = new CorrespondenceClient( correspondenceSettings );
		return new AltinnFacade( this.templateEngine, correspondenceClient, this.velocityModelFactory );
	}

	private VelocityTemplateEngine createTemplateEngine() {
		VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
		velocityTemplateEngine.setVelocityEngine( this.velocityEngine );
		return velocityTemplateEngine;
	}


	protected EmailFacade createEmailFacadeDocumentAlert( JavaMailSenderImpl javaMailSender, MessageTemplateAssembly templates ) {
		MailSender mailSender = createMailSender( javaMailSender );
		EmailFacadeDocumentAlert emailFacade = new EmailFacadeDocumentAlert( this.templateEngine, mailSender, this.velocityModelFactory, this.pdfGenerator );
		emailFacade.setBodyTemplate( templates.getBodyTemplate() );
		emailFacade.setBodyTemplateNoAttachment( templates.getBodyTemplateNoAttachment() );
		emailFacade.setSubjectTemplate( templates.getSubjectTemplate() );
		emailFacade.setPdfTemplate( templates.getPdfTemplate() );
		emailFacade.setReplyTo( templates.getReplyTo() );
		emailFacade.setToTemplate( templates.getToTemplate() );
		return emailFacade;
	}

	protected EmailFacade createEmailFacadeDocumentAttached( JavaMailSenderImpl javaMailSender, MessageTemplateAssembly templates ) {
		MailSender mailSender = createMailSender( javaMailSender );
		EmailFacade emailFacade = new EmailFacade( templateEngine, mailSender, this.velocityModelFactory );
		emailFacade.setBodyTemplate( templates.getBodyTemplate() );
		emailFacade.setBodyTemplateNoAttachment( templates.getBodyTemplateNoAttachment() );
		emailFacade.setSubjectTemplate( templates.getSubjectTemplate() );
		emailFacade.setReplyTo( templates.getReplyTo() );
		emailFacade.setToTemplate( templates.getToTemplate() );
		return emailFacade;
	}

	private MailSender createMailSender( JavaMailSenderImpl javaMailSender ) {
		MailSender mailSender = new MailSender();
		mailSender.setJavaMailSender( javaMailSender );
		return mailSender;
	}

	protected LdapFacade createLdapFacade( ContextSource ldapContextSource, String ldapFilterTemplate ) {
		LdapTemplate ldapTemplate = new LdapTemplate( ldapContextSource );
		LdapFacade ldapFacade = new LdapFacade( ldapTemplate, ldapFilterTemplate );
		return ldapFacade;
	}

	protected ForsendelsesArkiv createForsendelsesArkiv() {
		FileStore fileStore = new FileStore( this.archiveContext.getFileStorePath(), this.pdfGenerator );
		JdbcTemplate jdbcTemplate = new JdbcTemplate( this.archiveContext.getDataSource() );
		ForsendelsesArkiv forsendelsesArkiv = new ForsendelsesArkiv( fileStore, jdbcTemplate );
		forsendelsesArkiv.setFailedToPrintAlertWindowStartDay( this.printContext.getFailedToPrintAlertWindowStartDay() );
		forsendelsesArkiv.setFailedToPrintAlertWindowEndDay( this.printContext.getFailedToPrintAlertWindowEndDay() );
		return forsendelsesArkiv;
	}

	public void verify() {
		Field[] fields = this.getClass().getFields();
		for (Field field : fields) {
			String name = field.getName();
			try {
				field.get( this );
			} catch (Exception e) {
				throw new RuntimeException( "Undefined field: " + name, e );
			}
		}
	}

	static DriverManagerDataSource createTestDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName( "org.hsqldb.jdbcDriver" );
		dataSource.setUrl( "jdbc:hsqldb:mem:mindatabase" );
		dataSource.setUsername( "sa" );
		dataSource.setPassword( "" );
		return dataSource;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder( "{\n" );
		Class<? extends ServiceContext> clazz = this.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String name = field.getName();
			String value = "null";
			Object obj = null;
			try {
				obj = field.get( this );
				value = obj.toString();
			} catch (Exception e) {
				logger.warn( e );
			}
			sb.append( "  " ).append( name ).append( "=" ).append( value ).append( "\n" );
		}
		sb.append( "}" );
		return sb.toString();
	}

	public ForsendelsesArkiv getForsendelsesArkiv() {
		return forsendelsesArkiv;
	}

	public LdapFacade getLdapFacade() {
		return ldapFacade;
	}

	public EmailFacade getEmailFacadeNorgeDotNoDocumentAlert() {
		return emailFacadeNorgeDotNoDocumentAlert;
	}

	public EmailFacade getEmailFacadeNorgeDotNoDocumentAttached() {
		return emailFacadeNorgeDotNoDocumentAttached;
	}

	public EmailFacade getEmailFacade() {
		return emailFacade;
	}

	public PrintFacade getPrintFacade() {
		return printFacade;
	}

	public PdfGenerator getPdfGenerator() {
		return pdfGenerator;
	}

	public VelocityModelFactory getVelocityModelFactory() {
		return this.velocityModelFactory;
	}

	public AltinnFacade getAltinnFacade() {
		return this.altinnFacade;
	}

	public NorgeDotNoContext getNorgeDotNoContext() {
		return norgeDotNoContext;
	}

	public AltinnContext getAltinnContext() {
		return altinnContext;
	}

	public EmailContext getEmailContext() {
		return emailContext;
	}

	public PrintContext getPrintContext() {
		return printContext;
	}

	public DownloadContext getDownloadContext() {
		return downloadContext;
	}

	public ArchiveContext getArchiveContext() {
		return archiveContext;
	}

}
