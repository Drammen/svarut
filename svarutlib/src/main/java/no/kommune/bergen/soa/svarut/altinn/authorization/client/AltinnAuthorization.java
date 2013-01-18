package no.kommune.bergen.soa.svarut.altinn.authorization.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AltinnAuthorization {

	private static final Logger log = LoggerFactory.getLogger(AltinnAuthorization.class);

	private final AltinnAuthorizationDesicionPointExternalClient altinnAuthorizationDesicionPointExternalClient;

	public AltinnAuthorization(AltinnAuthorizationDesicionPointExternalSettings settings) {
		altinnAuthorizationDesicionPointExternalClient = new AltinnAuthorizationDesicionPointExternalClient(settings);
	}

	public boolean authorize(String fodselsNr, String orgNr) {
		return altinnAuthorizationDesicionPointExternalClient.authorizeAccessExternal(fodselsNr, orgNr);
	}

}
