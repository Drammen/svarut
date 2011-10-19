package no.kommune.bergen.soa.svarut.context;

import no.kommune.bergen.soa.svarut.util.DispatchWindow;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Holds configuration context regarding regular email distributions. Obs: Lead time is here to understand as the time a recipient
 * has to read the document before a regular amil fall-back distribution has to be invoked
 */
public class EmailContext {
	private JavaMailSenderImpl javaMailSender;
	private long leadTimeApost = 2, leadTimeBpost = 2, leadTimeRekommandert = 2;
	private MessageTemplateAssembly messageTemplateAssembly;
    private DispatchWindow dispatchWindow;

	public void verify() {
		if (javaMailSender == null) throw new RuntimeException( "Undefined field: javaMailSender" );
		if (messageTemplateAssembly == null) throw new RuntimeException( "Undefined field: messageTemplateAssembly" );
	}

	@Override
	public String toString() {
		return String.format( "{\njavaMailSender=%s\n leadTimeApost=%s\n leadTimeBpost=%s\n leadTimeRekommandert=%s\n messageTemplateAssembly=%s\n}", javaMailSender, leadTimeApost, leadTimeBpost, leadTimeRekommandert, messageTemplateAssembly );
	}

	public JavaMailSenderImpl getJavaMailSender() {
		return javaMailSender;
	}

	public void setJavaMailSender( JavaMailSenderImpl javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	public long getLeadTimeApost() {
		return leadTimeApost;
	}

	public void setLeadTimeApost( long leadTimeApost ) {
		this.leadTimeApost = leadTimeApost;
	}

	public long getLeadTimeBpost() {
		return leadTimeBpost;
	}

	public void setLeadTimeBpost( long leadTimeBpost ) {
		this.leadTimeBpost = leadTimeBpost;
	}

	public long getLeadTimeRekommandert() {
		return leadTimeRekommandert;
	}

	public void setLeadTimeRekommandert( long leadTimeRekommandert ) {
		this.leadTimeRekommandert = leadTimeRekommandert;
	}

	public MessageTemplateAssembly getMessageTemplateAssembly() {
		return messageTemplateAssembly;
	}

	public void setMessageTemplateAssembly( MessageTemplateAssembly messageTemplateAssembly ) {
		this.messageTemplateAssembly = messageTemplateAssembly;
	}

    public DispatchWindow getDispatchWindow() {
        return dispatchWindow;
    }

    public void setDispatchWindow(DispatchWindow dispatchWindow) {
        this.dispatchWindow = dispatchWindow;
    }
}
