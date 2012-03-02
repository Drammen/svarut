package no.kommune.bergen.soa.svarut.altin;

/** Melding som sendes til mottakerens Altinn postkasse */
public class CorrespondenceMessage {
	private String orgNr;
	private String messageSummary;
	private String messageTitle;
	private String messageBody;
	private String externalReference;

	public String getOrgNr() {
		return orgNr;
	}

	public void setOrgNr( String orgNr ) {
		this.orgNr = orgNr;
	}

	public String getMessageSummary() {
		return messageSummary;
	}

	public void setMessageSummary( String messageSummary ) {
		this.messageSummary = messageSummary;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle( String messageTitle ) {
		this.messageTitle = messageTitle;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody( String messageBody ) {
		this.messageBody = messageBody;
	}

	public String getExternalReference() {
		return externalReference;
	}

	public void setExternalReference( String externalReference ) {
		this.externalReference = externalReference;
	}

}
