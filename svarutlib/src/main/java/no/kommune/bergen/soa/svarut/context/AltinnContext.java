package no.kommune.bergen.soa.svarut.context;

import no.kommune.bergen.soa.svarut.altin.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.util.DispatchWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Benyttes som bærer av kofigurasjon for Altinn distribusjoner. Obs: LeadTime betyr her antall dager mottaker har på å lese et
 * dokument før dokumentet må sendes i posten
 */
@Component("altinnContext")
public class AltinnContext {


	@Autowired
	private CorrespondenceSettings correspondenceSettings;

	private long leadTimeApost = 2, leadTimeBpost = 2, leadTimeRekommandert = 1;
	private MessageTemplateAssembly messageTemplateAssembly;

	@Resource(name = "altinnDispatchWindow")
	private DispatchWindow dispatchWindow;

	public void verify() {
		if (correspondenceSettings == null) throw new RuntimeException( "Undefined field: correspondenceSettings in AltinnContext" );
		if (messageTemplateAssembly == null) throw new RuntimeException( "Undefined field: messageTemplateAssembly in AltinnContext" );
	}

	@Override
	public String toString() {
		return String.format( "{\n  correspondenceSettings=%s\n leadTimeApost=%s\n leadTimeBpost=%s\n leadTimeRekommandert=%s\n \n}", correspondenceSettings, leadTimeApost, leadTimeBpost, leadTimeRekommandert);
	}

	public CorrespondenceSettings getCorrespondenceSettings() {
		return correspondenceSettings;
	}

	public void setCorrespondenceSettings( CorrespondenceSettings correspondenceSettings ) {
		this.correspondenceSettings = correspondenceSettings;
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

	public MessageTemplateAssembly getMessageTemplateAssembly() {
			return messageTemplateAssembly;
	}

	public void setMessageTemplateAssembly( MessageTemplateAssembly messageTemplateAssembly ) {
		this.messageTemplateAssembly = messageTemplateAssembly;
	}
}
