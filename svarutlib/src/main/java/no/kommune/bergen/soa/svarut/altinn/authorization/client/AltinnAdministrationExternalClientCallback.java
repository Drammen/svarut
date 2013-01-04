package no.kommune.bergen.soa.svarut.altinn.authorization.client;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class AltinnAdministrationExternalClientCallback implements CallbackHandler {

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
	    WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
	    pc.setPassword("UO6pXuIt");
	}
}