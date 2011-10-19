package no.kommune.bergen.soa.svarut.context;

import no.kommune.bergen.soa.svarut.util.DispatchWindow;
import org.springframework.ldap.core.ContextSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Benyttes som bærer av kofigurasjon for minside-Norge.no distribusjoner. Obs: LeadTime betyr her antall dager mottaker har på å
 * lese et dokument før dokumentet må sendes i posten
 */
public class NorgeDotNoContext {
	private ContextSource ldapContextSource;
	private JavaMailSenderImpl javaMailSender;
	private long leadTimeApost = 2, leadTimeBpost = 2, leadTimeRekommandert = 2;
	private String ldapFilterTemplate;
	private MessageTemplateAssembly messageTemplateAssembly;
    private DispatchWindow dispatchWindow;

	public void verify() {
		if (ldapContextSource == null) throw new RuntimeException( "Undefined field: ldapContextSource" );
		if (javaMailSender == null) throw new RuntimeException( "Undefined field: javaMailSender" );
		if (ldapFilterTemplate == null) throw new RuntimeException( "Undefined field: ldapFilterTemplate" );
		if (messageTemplateAssembly == null) throw new RuntimeException( "Undefined field: messageTemplateAssembly" );
	}

	@Override
	public String toString() {
		return String.format( "{\nldapContextSource=%s\n javaMailSender=%s\n leadTimeApost=%s\n leadTimeBpost=%s\n leadTimeRekommandert=%s\n ldapFilterTemplate=%s\n messageTemplateAssembly=%s\n}", ldapContextSource, javaMailSender, leadTimeApost,
				leadTimeBpost, leadTimeRekommandert, ldapFilterTemplate, messageTemplateAssembly );
	}

	public ContextSource getLdapContextSource() {
		return ldapContextSource;
	}

	public void setLdapContextSource( ContextSource ldapContextSource ) {
		this.ldapContextSource = ldapContextSource;
	}

	public JavaMailSenderImpl getJavaMailSender() {
		return javaMailSender;
	}

	public void setJavaMailSender( JavaMailSenderImpl javaMailSender ) {
		this.javaMailSender = javaMailSender;
	}

	public String getLdapFilterTemplate() {
		return ldapFilterTemplate;
	}

	public void setLdapFilterTemplate( String ldapFilterTemplate ) {
		this.ldapFilterTemplate = ldapFilterTemplate;
	}

	public MessageTemplateAssembly getMessageTemplateAssembly() {
		return messageTemplateAssembly;
	}

	public void setMessageTemplateAssembly( MessageTemplateAssembly messageTemplateAssembly ) {
		this.messageTemplateAssembly = messageTemplateAssembly;
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

    public DispatchWindow getDispatchWindow() {
        return dispatchWindow;
    }

    public void setDispatchWindow(DispatchWindow dispatchWindow) {
        this.dispatchWindow = dispatchWindow;
    }
}
