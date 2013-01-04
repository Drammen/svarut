package no.kommune.bergen.soa.svarut.altinn.correspondence;

import no.kommune.bergen.soa.svarut.altinn.AltinnCommonSettings;
import no.kommune.bergen.soa.svarut.altinn.AltinnServiceSettings;

import org.constretto.annotation.Configuration;
import org.springframework.stereotype.Component;

/** Holds context/configuration regarding Altinn message exchange */
@Component
public class CorrespondenceSettings extends AltinnCommonSettings implements AltinnServiceSettings{
	@Configuration(expression = "altinn.notificationType")
	private String notificationType;

	private String languageCode = "1044";
	@Configuration(expression = "altinn.serviceCode")
	private String serviceCode;

	@Configuration(expression = "altinn.endpoint")
	private String endpoint;

	private String serviceEdition = "1";

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType( String notificationType ) {
		this.notificationType = notificationType;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode( String languageCode ) {
		this.languageCode = languageCode;
	}

	@Override
	public String getServiceCode() {
		return serviceCode;
	}

	@Override
	public void setServiceCode( String serviceCode ) {
		this.serviceCode = serviceCode;
	}

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public void setEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}

	@Override
	public String getServiceEdition() {
		return serviceEdition;
	}

	@Override
	public void setServiceEdition( String serviceEdition ) {
		this.serviceEdition = serviceEdition;
	}
}
