package no.kommune.bergen.soa.svarut.context;

import no.kommune.bergen.soa.svarut.altin.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.util.DispatchWindow;

/**
 * Benyttes som bærer av kofigurasjon for Altinn distribusjoner. Obs: LeadTime betyr her antall dager mottaker har på å lese et
 * dokument før dokumentet må sendes i posten
 */
public class AltinnContext {
	private CorrespondenceSettings correspondenceSettings;
	private long leadTimeApost = 2, leadTimeBpost = 2, leadTimeRekommandert = 2;
    private DispatchWindow dispatchWindow;

	public void verify() {
		if (correspondenceSettings == null) throw new RuntimeException( "Undefined field: correspondenceSettings" );
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
}
