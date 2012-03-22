package no.kommune.bergen.soa.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;

public class MailSender {

	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

	private static final String MAIL_ENCODING = "UTF-8";
	private static final File[] EMPTY_ATTACHMENTS = new File[0];

	private JavaMailSenderImpl javaMailSender = null;

	/** Send an email to a single recipient. */
	@SuppressWarnings("JavaDoc")
	public void sendEmail( final String to, final String from, final String subject, final String body, final File[] attachments ) {
		List<String> recipient = new ArrayList<String>();
		recipient.add( to );

		if (attachments != null && attachments.length > 0)
			sendMimeMessage( recipient, from, subject, body, attachments );
		else
			sendMimeMessage( recipient, from, subject, body, EMPTY_ATTACHMENTS);
	}

	/** Send an email to a list of recipients. */
	@SuppressWarnings({"unused", "JavaDoc"})
	public void sendEmail( final List<String> recipients, final String from, final String subject, final String body, final File[] attachments ) {
		if (attachments != null && attachments.length > 0)
			sendMimeMessage( recipients, from, subject, body, attachments );
		else
			sendMimeMessage( recipients, from, subject, body, EMPTY_ATTACHMENTS );
	}

	@SuppressWarnings("unused")
	private void sendPlainMessage( final List<String> recipients, final String from, final String subject, final String body ) {
		for (String to : recipients) {
			logger.debug("Sending email to: {} from: {} subject: {}", new Object[]{to, from, subject});
			if (to != null && to.length() != 0 && to.indexOf('@') > -1) {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to.split(","));
				message.setSubject(subject);
				message.setText(body);
				javaMailSender.send(message);
			} else
				throw new IllegalArgumentException("Bad email to address(es). Email to: " + to);
		}
	}

	private void sendMimeMessage( final List<String> recipients, final String from, final String subject, final String body, final File[] attachments ) {
		for (String recipient : recipients) {
			final String to = recipient;
			Assert.isTrue( to != null && to.length() != 0 && to.indexOf( '@' ) > -1 );
			logger.debug("Sending email to: {} from: {} subject: {}", new Object[]{to, from, subject});
			javaMailSender.send(new MimeMessagePreparator() {
				@Override
				public void prepare( MimeMessage mimeMessage ) throws MessagingException {
					MimeMessageHelper message;
					message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, MAIL_ENCODING);
					message.setFrom( from );
					message.setTo( InternetAddress.parse( to ) );
					message.setSubject( subject );
					message.setText( body, false );

					for (File attachment : attachments)
						message.addAttachment( attachment.getName(), attachment );

					logger.debug("Sending email: {}", subject);
				}
			} );
		}
	}

	public void setJavaMailSender( JavaMailSenderImpl javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}
}
