package no.kommune.bergen.soa.svarut.altin;

import org.constretto.annotation.Configuration;
import org.springframework.stereotype.Component;

/** Holds context/configuration regarding Altinn message exchange */
@Component
public class CorrespondenceSettings {
	@Configuration(expression = "altinn.notificationType")
	private String notificationType;
	@Configuration(expression = "altinn.fromAddress")
	private String fromAddress;

	private String languageCode = "1044";
	@Configuration(expression = "altinn.serviceCode")
	private String serviceCode;

	@Configuration(expression = "altinn.endpoint")
	private String endpoint;

	@Configuration(expression = "altinn.systemUserName")
	private String systemUserName;
	@Configuration(expression = "altinn.systemPassword")
	private String systemPassword;
	@Configuration(expression = "altinn.systemUserCode")
	private String systemUserCode;
	private String serviceEdition = "1";
	@Configuration(expression = "altinn.subjectTemplate")
	private String subjectTemplate = "";
	@Configuration(expression = "altinn.bodyTemplate")
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
