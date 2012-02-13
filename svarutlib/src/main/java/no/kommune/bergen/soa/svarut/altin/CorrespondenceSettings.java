package no.kommune.bergen.soa.svarut.altin;

/** Holds context/configuration regarding Altinn message exchange */
public class CorrespondenceSettings {
	private String notificationType = "Melding fra Bergen kommune";
	private String fromAddress = "einarvalen@gmail.com";
	private String languageCode = "1044";
	private String serviceCode = "2930";
	private String endpoint = "https://tt02.altinn.basefarm.net/ServiceEngineExternal/CorrespondenceAgencyExternalBasic.svc";
	private String systemUserName = ""; // Service login
	private String systemPassword = ""; // Service login
	private String systemUserCode = ""; // Service login
	private String serviceEdition = "1";
	private String subjectTemplate = "";
	private String bodyTemplate = "";

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType( String notificationType ) {
		this.notificationType = notificationType;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress( String fromAddress ) {
		this.fromAddress = fromAddress;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode( String languageCode ) {
		this.languageCode = languageCode;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode( String serviceCode ) {
		this.serviceCode = serviceCode;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}

	public String getSystemUserName() {
		return systemUserName;
	}

	public void setSystemUserName( String systemUserName ) {
		this.systemUserName = systemUserName;
	}

	public String getSystemPassword() {
		return systemPassword;
	}

	public void setSystemPassword( String systemPassword ) {
		this.systemPassword = systemPassword;
	}

	public String getSystemUserCode() {
		return systemUserCode;
	}

	public void setSystemUserCode( String systemUserCode ) {
		this.systemUserCode = systemUserCode;
	}

	public String getServiceEdition() {
		return serviceEdition;
	}

	public void setServiceEdition( String serviceEdition ) {
		this.serviceEdition = serviceEdition;
	}

	public String getSubjectTemplate() {
		return subjectTemplate;
	}

	public void setSubjectTemplate( String subjectTemplate ) {
		this.subjectTemplate = subjectTemplate;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	public void setBodyTemplate( String bodyTemplate ) {
		this.bodyTemplate = bodyTemplate;
	}

}
