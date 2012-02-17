package no.kommune.bergen.soa.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;

public class MailSender {

	static final Logger logger = Logger.getLogger( MailSender.class );
	JavaMailSenderImpl javaMailSender = null;
	String encoding = "UTF-8";

	/** Send an email to a single recipient. */
	public void sendEmail( final String to, final String from, final String subject, final String body, final File[] attachments ) {
		List<String> recipient = new ArrayList<String>();
		recipient.add( to );

		if (null != attachments && attachments.length > 0) {
			sendMimeMessage( recipient, from, subject, body, attachments );
		} else {
			final File[] emptyAttachments = new File[0];
			sendMimeMessage( recipient, from, subject, body, emptyAttachments);
		}
	}

	/** Send an email to a list of recipients. */
	public void sendEmail( final List<String> recipients, final String from, final String subject, final String body, final File[] attachments ) {
		if (null != attachments && attachments.length > 0) {
			sendMimeMessage( recipients, from, subject, body, attachments );
		} else {
			final File[] emptyAttachments = new File[0];
			sendMimeMessage( recipients, from, subject, body, emptyAttachments );
		}
	}

	@SuppressWarnings("unused")
	private void sendPlainMessage( final List<String> recipients, final String from, final String subject, final String body ) {
		for (String to : recipients) {
			if (logger.isDebugEnabled()) logger.debug( "Sending email to:" + to + " from:" + from + " subject:" + subject );
			Assert.isTrue( to != null && to.length() != 0 && to.indexOf( '@' ) > -1 );
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom( from );
			message.setTo( to.split( "," ) );
			message.setSubject( subject );
			message.setText( body );
			this.javaMailSender.send( message );
		}
	}

	private void sendMimeMessage( final List<String> recipients, final String from, final String subject, final String body, final File[] attachments ) {
		for (String recipient : recipients) {
			final String to = recipient;
			Assert.isTrue( to != null && to.length() != 0 && to.indexOf( '@' ) > -1 );
			if (logger.isDebugEnabled()) logger.debug( "Sending email to:" + to + " from:" + from + " subject:" + subject );
			this.javaMailSender.send( new MimeMessagePreparator() {
				@Override
				public void prepare( MimeMessage mimeMessage ) throws MessagingException {
					MimeMessageHelper message = null;
					message = new MimeMessageHelper( mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, encoding );
					message.setFrom( from );
					message.setTo( InternetAddress.parse( to ) );
					message.setSubject( subject );
					message.setText( body, false );

					for (File attachment : attachments) {
						message.addAttachment( attachment.getName(), attachment );
					}
					if (logger.isDebugEnabled()) logger.debug( "Sending email: " + subject );
				}
			} );
		}
	}

	public void setJavaMailSender( JavaMailSenderImpl javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	public JavaMailSenderImpl getJavaMailSender() {
		return this.javaMailSender;
	}

	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}
}
