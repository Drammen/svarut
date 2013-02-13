package no.kommune.bergen.soa.svarut.altinn.administration.external.client;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;
import org.constretto.ConstrettoBuilder;
import org.constretto.ConstrettoConfiguration;
import org.springframework.core.io.DefaultResourceLoader;

public class AltinnAdministrationExternalClientCallback implements CallbackHandler {

	String password;

	public AltinnAdministrationExternalClientCallback() {
		ConstrettoConfiguration config = new ConstrettoBuilder().createPropertiesStore().addResource(new DefaultResourceLoader().getResource("classpath:application.properties")).done().getConfiguration();
		password = config.evaluateToString("altinn.systemPassword");
	}

	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		pc.setPassword(password);
	}
}