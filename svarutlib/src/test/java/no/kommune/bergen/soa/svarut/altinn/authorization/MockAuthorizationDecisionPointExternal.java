package no.kommune.bergen.soa.svarut.altinn.authorization;

import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalClient;
import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalSettings;

public class MockAuthorizationDecisionPointExternal extends AltinnAuthorizationDesicionPointExternalClient {

	private final String authorizedFnr = "02035701829";
	private final String authorizedOrgnr = "910824929";

	public MockAuthorizationDecisionPointExternal(AltinnAuthorizationDesicionPointExternalSettings settings) {
		super(settings);
	}

	@Override
	public boolean authorizeAccessExternal(String fodselsNr, String orgNr) {
		if(fodselsNr.equals(authorizedFnr) && orgNr.equals(authorizedOrgnr))
			return true;

		return false;
	}
}
