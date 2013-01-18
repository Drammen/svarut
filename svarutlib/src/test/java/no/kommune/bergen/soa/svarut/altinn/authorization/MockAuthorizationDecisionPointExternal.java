package no.kommune.bergen.soa.svarut.altinn.authorization;

import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalClient;
import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalSettings;

public class MockAuthorizationDecisionPointExternal extends AltinnAuthorizationDesicionPointExternalClient {

	private final AltinnAuthorizationDesicionPointExternalSettings settings;

	public MockAuthorizationDecisionPointExternal(AltinnAuthorizationDesicionPointExternalSettings settings) {
		super(settings);
		this.settings = settings;
	}

	@Override
	protected void setupAuthorizationDesicionPointExternalServices() {
		// Do nothing
	}

	@Override
	public boolean authorizeAccessExternal(String fodselsNr, String orgNr) {
		return true;
	}
}
