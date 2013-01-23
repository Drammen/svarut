package no.kommune.bergen.soa.svarut.altinn.authorization.client;

import no.kommune.bergen.soa.svarut.altinn.AltinnCommonSettings;
import no.kommune.bergen.soa.svarut.altinn.AltinnServiceSettings;

import org.constretto.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class AltinnAuthorizationDesicionPointExternalSettings extends AltinnCommonSettings implements AltinnServiceSettings {

	@Configuration(expression = "altinn.authorization.endpoint")
	String endpoint;
	String serviceEdition;
	@Configuration(expression = "altinn.authorization.environment")
	String environment;

	@Override
	public String getEndpoint() {
		return endpoint;
	}

	@Override
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@Override
	public String getServiceEdition() {
		return serviceEdition;
	}

	@Override
	public void setServiceEdition(String serviceEdition) {
		this.serviceEdition = serviceEdition;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
}