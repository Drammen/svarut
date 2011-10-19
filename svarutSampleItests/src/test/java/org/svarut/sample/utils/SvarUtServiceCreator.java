package org.svarut.sample.utils;

import no.kommune.bergen.svarut.v1.SvarUtService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class SvarUtServiceCreator {

	public static SvarUtService getService() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(SvarUtService.class);
		factory.setAddress(Constants.webContainer + "/service/svarut/sample/SvarUtSampleService-v1?wsdl");
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("mtom-enabled", Boolean.TRUE);
		factory.setProperties(props);
		return (SvarUtService) factory.create();
	}

	public static void waitTillFinishedWorking(){
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(Constants.webContainer + "/service/rest/forsendelsesservice/waitTillFinished");
		try {
			int result = client.executeMethod(method);
		} catch (IOException e) {

		}
	}
}
