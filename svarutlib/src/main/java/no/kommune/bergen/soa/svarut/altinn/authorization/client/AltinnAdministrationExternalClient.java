package no.kommune.bergen.soa.svarut.altinn.authorization.client;

import java.util.HashMap;
import java.util.Map;

import no.altinn.schemas.services.authorization.administration._2012._11.ExternalReporteeBEList;
import no.altinn.services.authorization.administration._2010._10.IAuthorizationAdministrationExternal;
import no.altinn.services.authorization.administration._2010._10.IAuthorizationAdministrationExternalGetReporteesAltinnFaultFaultFaultMessage;

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
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;


public class AltinnAdministrationExternalClient {

	private IAuthorizationAdministrationExternal iAuthorizationAdministrationExternal;
	private final AltinnAdministrationExternalSettings authorizationServiceSettings;

	public AltinnAdministrationExternalClient(AltinnAdministrationExternalSettings settings) {
		this.authorizationServiceSettings = settings;
		setupAdministrationExternalServices();
	}


	private void setupAdministrationExternalServices() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		// Set requests to run on SOAP 1.2
		factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());

		factory.setServiceClass(IAuthorizationAdministrationExternal.class);
		//TODO Hente adresse fra konfigurasjon
		factory.setAddress(authorizationServiceSettings.getEndpoint());
		factory.getFeatures().add(new WSAddressingFeature());

		iAuthorizationAdministrationExternal = (IAuthorizationAdministrationExternal) factory.create();

		Client client = ClientProxy.getClient(iAuthorizationAdministrationExternal);

		// WS-security settings
		Endpoint cxfEndpoint = client.getEndpoint();
		WSS4JOutInterceptor wssOut = null;
		Map<String,Object> outProps = new HashMap<String, Object>();
		outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
		outProps.put(WSHandlerConstants.USER, authorizationServiceSettings.getSystemUserName());
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

	public ExternalReporteeBEList getAvgivere(String fodselsNr) {
		ExternalReporteeBEList list = null;
		try {
			list = iAuthorizationAdministrationExternal.getReportees(fodselsNr, true, true, 100);
		} catch (IAuthorizationAdministrationExternalGetReporteesAltinnFaultFaultFaultMessage e) {
			e.printStackTrace();
			System.out.println(e.getStackTrace());
		}
		return list;
	}
}
