package no.kommune.bergen.soa.svarut.altinn;

import org.constretto.annotation.Configuration;
import org.springframework.stereotype.Component;

/** Holds common context/configuration regarding Altinn communication */
@Component
public class AltinnCommonSettings {
	@Configuration(expression = "altinn.fromAddress")
	private String fromAddress;

	@Configuration(expression = "altinn.serviceCode")
	private String serviceCode;

	@Configuration(expression = "altinn.systemUserName")
	private String systemUserName;
	@Configuration(expression = "altinn.systemPassword")
	private String systemPassword;
	@Configuration(expression = "altinn.systemUserCode")
	private String systemUserCode;

	@Configuration(expression = "altinn.subjectTemplate")
	private String subjectTemplate = "";
	@Configuration(expression = "altinn.bodyTemplate")
	private String bodyTemplate = "";

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress( String fromAddress ) {
		this.fromAddress = fromAddress;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode( String serviceCode ) {
		this.serviceCode = serviceCode;
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

	public String getSystemUserCode() {
		return systemUserCode;
	}

	public void setSystemUserCode( String systemUserCode ) {
		this.systemUserCode = systemUserCode;
	}

}
