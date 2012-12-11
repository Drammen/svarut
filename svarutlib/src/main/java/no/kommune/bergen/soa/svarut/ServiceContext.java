package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.common.util.VelocityTemplateEngine;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.context.*;
import no.kommune.bergen.soa.svarut.dao.FileStore;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.lang.reflect.Field;

/**
 * Holding tank for context
 */
public class ServiceContext {

	private static final Logger log = LoggerFactory.getLogger(ServiceContext.class);

	final ForsendelsesArkiv forsendelsesArkiv;
	final EmailFacade emailFacade;
	PrintFacade printFacade;
	final PdfGenerator pdfGenerator;
	final VelocityModelFactory velocityModelFactory;
	private final AltinnFacade altinnFacade;
	private final TemplateEngine templateEngine;
	private final VelocityEngine velocityEngine;

	private final AltinnContext altinnContext;
	private final EmailContext emailContext;
	private final PrintContext printContext;
	private final DownloadContext downloadContext;
	private final ArchiveContext archiveContext;

	public ServiceContext(VelocityEngine velocityEngine, AltinnContext altinnContext, EmailContext emailContext,
						  PrintContext printContext, DownloadContext downloadContext, ArchiveContext archiveContext) {
		this.velocityEngine = velocityEngine;
		this.altinnContext = altinnContext;
		this.emailContext = emailContext;
		this.printContext = printContext;
		this.downloadContext = downloadContext;
		this.archiveContext = archiveContext;
		verifyParams();
		templateEngine = createTemplateEngine();
		velocityModelFactory = createVelocityModelFactory();
		pdfGenerator = new SvarUtPdfGenerator(this.archiveContext.getTempDir());
		forsendelsesArkiv = createForsendelsesArkiv();
		emailFacade = createEmailFacadeDocumentAttached(this.emailContext.getJavaMailSender(), this.emailContext.getMessageTemplateAssembly());
		altinnFacade = createAltinnFacade(this.altinnContext.getCorrespondenceSettings());
		printFacade = createPrintFacade(this.printContext.getFrontPageTemplate(), this.printContext.getPrintServiceProvider());
		verify();
	}

	private void verifyParams() {
		altinnContext.verify();
		emailContext.verify();
		printContext.verify();
		downloadContext.verify();
		archiveContext.verify();
	}

	private PrintFacade createPrintFacade(String frontPageTemplate, PrintServiceProvider printServiceProvider) {
		return new PrintFacade(pdfGenerator, frontPageTemplate, printServiceProvider, getVelocityModelFactory());
	}

	private VelocityModelFactory createVelocityModelFactory() {
		return new VelocityModelFactory(downloadContext.getServletPathTemplate(), downloadContext.getPdfLinkText(), downloadContext.getHelpLink(),
				downloadContext.getHelpLinkText(), downloadContext.getReaderDownloadLink(), downloadContext.getReaderDownloadLinkText());
	}

	private AltinnFacade createAltinnFacade(CorrespondenceSettings correspondenceSettings) {
		CorrespondenceClient correspondenceClient = new CorrespondenceClient(correspondenceSettings);
		return new AltinnFacade(templateEngine, correspondenceClient, velocityModelFactory);
	}

	private VelocityTemplateEngine createTemplateEngine() {
		VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
		velocityTemplateEngine.setVelocityEngine(velocityEngine);
		return velocityTemplateEngine;
	}

	@SuppressWarnings("unused")
	protected EmailFacade createEmailFacadeDocumentAlert(JavaMailSenderImpl javaMailSender, MessageTemplateAssembly templates) {
		MailSender mailSender = createMailSender(javaMailSender);
		EmailFacadeDocumentAlert emailFacade = new EmailFacadeDocumentAlert(templateEngine, mailSender, velocityModelFactory, pdfGenerator);
		emailFacade.setBodyTemplate(templates.getBodyTemplate());
		emailFacade.setBodyTemplateNoAttachment(templates.getBodyTemplateNoAttachment());
		emailFacade.setSubjectTemplate(templates.getSubjectTemplate());
		emailFacade.setPdfTemplate(templates.getPdfTemplate());
		emailFacade.setReplyTo(templates.getReplyTo());
		emailFacade.setToTemplate(templates.getToTemplate());
		return emailFacade;
	}

	protected EmailFacade createEmailFacadeDocumentAttached(JavaMailSenderImpl javaMailSender, MessageTemplateAssembly templates) {
		MailSender mailSender = createMailSender(javaMailSender);
		EmailFacade emailFacade = new EmailFacade(templateEngine, mailSender, velocityModelFactory);
		emailFacade.setBodyTemplate(templates.getBodyTemplate());
		emailFacade.setBodyTemplateNoAttachment(templates.getBodyTemplateNoAttachment());
		emailFacade.setSubjectTemplate(templates.getSubjectTemplate());
		emailFacade.setReplyTo(templates.getReplyTo());
		emailFacade.setToTemplate(templates.getToTemplate());
		return emailFacade;
	}

	private MailSender createMailSender(JavaMailSenderImpl javaMailSender) {
		MailSender mailSender = new MailSender();
		mailSender.setJavaMailSender(javaMailSender);
		return mailSender;
	}

	protected ForsendelsesArkiv createForsendelsesArkiv() {
		FileStore fileStore = new FileStore(archiveContext.getFileStorePath(), pdfGenerator);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(archiveContext.getDataSource());
		ForsendelsesArkiv forsendelsesArkiv = new ForsendelsesArkiv(fileStore, jdbcTemplate);
		forsendelsesArkiv.setFailedToPrintAlertWindowStartDay(printContext.getFailedToPrintAlertWindowStartDay());
		forsendelsesArkiv.setFailedToPrintAlertWindowEndDay(printContext.getFailedToPrintAlertWindowEndDay());
		return forsendelsesArkiv;
	}

	public void verify() {
		Field[] fields = getClass().getFields();
		for (Field field : fields) {
			String name = field.getName();
			try {
				field.get(this);
			} catch (Exception e) {
				throw new RuntimeException("Undefined field: " + name, e);
			}
		}
	}

	static DriverManagerDataSource createTestDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:mindatabase");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{\n");
		Class<? extends ServiceContext> clazz = getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			String name = field.getName();
			String value = "null";
			Object obj;
			try {
				obj = field.get(this);
				value = obj.toString();
			} catch (Exception e) {
				log.warn("", e);
			}
			sb.append("  ").append(name).append("=").append(value).append("\n");
		}
		sb.append("}");
		return sb.toString();
	}

	public ForsendelsesArkiv getForsendelsesArkiv() {
		return forsendelsesArkiv;
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
		return velocityModelFactory;
	}

	public AltinnFacade getAltinnFacade() {
		return altinnFacade;
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

	@SuppressWarnings("unused")
	public DownloadContext getDownloadContext() {
		return downloadContext;
	}

	public ArchiveContext getArchiveContext() {
		return archiveContext;
	}
}
