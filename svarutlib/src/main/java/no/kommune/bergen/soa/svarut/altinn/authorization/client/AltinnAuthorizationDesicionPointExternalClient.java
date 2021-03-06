package no.kommune.bergen.soa.svarut.altinn.authorization.client;

import java.util.HashMap;
import java.util.Map;

import no.altinn.services.authorization.decisionpoint._2010._10.IAuthorizationDecisionPointExternal;
import no.altinn.services.authorization.decisionpoint._2010._10.IAuthorizationDecisionPointExternalAuthorizeAccessExternalAltinnFaultFaultFaultMessage;
import no.kommune.bergen.soa.svarut.altinn.administration.external.client.AltinnAdministrationExternalClientCallback;
import no.kommune.bergen.soa.svarut.altinn.authorization.xacml.AltinnAuthorizationDesicionPointExternalXACMLUtil;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;

public class AltinnAuthorizationDesicionPointExternalClient {

	public static final Logger log = Logger.getLogger(AltinnAuthorizationDesicionPointExternalClient.class);
	private IAuthorizationDecisionPointExternal iAuthorizationDecisionPointExternal;
	private final AltinnAuthorizationDesicionPointExternalSettings settings;

	public AltinnAuthorizationDesicionPointExternalClient(AltinnAuthorizationDesicionPointExternalSettings settings) {
		this.settings = settings;
		setupAuthorizationDesicionPointExternalServices();
	}

	protected void setupAuthorizationDesicionPointExternalServices() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		// Set requests to run on SOAP 1.2
		factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");

		factory.getInInterceptors().add(new LoggingInInterceptor());
		//		factory.getOutInterceptors().add(new CdataWriterInterceptor("XACMLRequest"));
		factory.getOutInterceptors().add(new LoggingOutInterceptor());

		factory.setServiceClass(IAuthorizationDecisionPointExternal.class);
		factory.setAddress(settings.getEndpoint());
		factory.getFeatures().add(new WSAddressingFeature());

		iAuthorizationDecisionPointExternal = (IAuthorizationDecisionPointExternal) factory.create();

		Client client = ClientProxy.getClient(iAuthorizationDecisionPointExternal);

		// WS-security settings
		Endpoint cxfEndpoint = client.getEndpoint();
		WSS4JOutInterceptor wssOut = null;
		Map<String,Object> outProps = new HashMap<String, Object>();
		outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		outProps.put(WSHandlerConstants.USER, settings.getSystemUserName());
		outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
		outProps.put(WSHandlerConstants.ADD_UT_ELEMENTS, WSConstants.NONCE_LN + " " + WSConstants.CREATED_LN);
		outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, AltinnAdministrationExternalClientCallback.class.getName());
		wssOut = new WSS4JOutInterceptor(outProps);
		cxfEndpoint.getOutInterceptors().add(wssOut);

		// Proxy settings
		HTTPConduit http = (HTTPConduit) client.getConduit();
		HTTPClientPolicy httpClientPolicy = http.getClient();
		httpClientPolicy.setConnectionTimeout(5000);
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setReceiveTimeout(8000);
		httpClientPolicy.setAcceptEncoding("UTF-8");
		if ("true".equalsIgnoreCase(System.getProperty("proxySet"))) {
			httpClientPolicy.setProxyServer(System.getProperty("http.proxyHost"));
			httpClientPolicy.setProxyServerPort(Integer.parseInt(System.getProperty("http.proxyPort")));
			httpClientPolicy.setNonProxyHosts(System.getProperty("http.nonProxyHosts"));
		}
		http.setClient(httpClientPolicy);
	}

	public boolean authorizeAccessExternal(String fodselsNr, String orgNr) {

		boolean authorized = false;

		String xacmlRequest = AltinnAuthorizationDesicionPointExternalXACMLUtil.createXACMLRequest(fodselsNr, orgNr, settings.getServiceCode(), settings.getServiceEdition(), settings.getEnvironment());
		log.debug("Generated XACML request:\n" + xacmlRequest);
		String xacmlResponse = null;
		try {
			xacmlResponse = iAuthorizationDecisionPointExternal.authorizeAccessExternal(xacmlRequest);
		} catch (IAuthorizationDecisionPointExternalAuthorizeAccessExternalAltinnFaultFaultFaultMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		log.debug("Altinn service XACML response:\n" + xacmlResponse);
		authorized = AltinnAuthorizationDesicionPointExternalXACMLUtil.parseXACMLResponseAndVerifyPermitted(xacmlResponse);

		return authorized;
	}
}
