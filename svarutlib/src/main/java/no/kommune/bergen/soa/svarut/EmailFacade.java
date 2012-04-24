package no.kommune.bergen.soa.svarut;

import java.io.File;
import java.util.Map;

import no.kommune.bergen.soa.common.util.MailSender;
import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Sending av vanlig epost */
public class EmailFacade {
	private static final Log log = LogFactory.getLog( EmailFacade.class );
	protected static final String U = "TEMPLATE-UNDEFINED";
	private String subjectTemplate = U, bodyTemplate = U, bodyTemplateNoAttachment = U;
	final String pdfTemplate = U;
	private String replyTo = U, toTemplate = U;
	public final TemplateEngine templateEngine;
	public MailSender mailSender;
	public final VelocityModelFactory modelFactory;

	public EmailFacade( TemplateEngine templateEngine, MailSender mailSender, VelocityModelFactory modelFactory ) {
		this.templateEngine = templateEngine;
		this.mailSender = mailSender;
		this.modelFactory = modelFactory;
	}

	public void send( Forsendelse f ) {
		String fnr = f.getFnr();
		Map<String, String> model = modelFactory.createModel( f );
		if (log.isDebugEnabled()) log.debug( "send(" + f.getId() + ")" );
		String subject = templateEngine.merge( model, this.subjectTemplate );
		File[] attachments = createAttachments( f, model );
		String bodyTemplate = hasAttachments( attachments ) ? this.bodyTemplate : this.bodyTemplateNoAttachment;
		String body = templateEngine.merge( model, bodyTemplate );
		String to = (isAltinn( f )) ? String.format( toTemplate, fnr ) : f.getEmail();
		String replyTo = (f.getReplyTo() == null) ? this.replyTo : f.getReplyTo();
		mailSender.sendEmail( to, replyTo, subject, body, attachments );
	}

	private boolean isAltinn( Forsendelse f ) {
		String shipmentPolicy = f.getShipmentPolicy();
		return shipmentPolicy != null && shipmentPolicy.contains( "Altinn" );
	}

	private boolean hasAttachments( File[] attachments ) {
		return (attachments != null && attachments.length > 0);
	}

	protected File[] createAttachments( Forsendelse f, Map<String, String> model ) {
		if (f == null || f.getFile() == null) return null;
		return new File[] { f.getFile() };
	}

	public void setSubjectTemplate( String subjectTemplate ) {
		this.subjectTemplate = subjectTemplate;
	}

	public void setBodyTemplate( String bodyTemplate ) {
		this.bodyTemplate = bodyTemplate;
	}

	public void setReplyTo( String replyTo ) {
		this.replyTo = replyTo;
	}

	public void setToTemplate( String toTemplate ) {
		this.toTemplate = toTemplate;
	}

	public String getSubjectTemplate() {
		return subjectTemplate;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public String getToTemplate() {
		return toTemplate;
	}

	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	public void setBodyTemplateNoAttachment( String bodyTemplateNoAttachment ) {
		this.bodyTemplateNoAttachment = bodyTemplateNoAttachment;
	}

}
