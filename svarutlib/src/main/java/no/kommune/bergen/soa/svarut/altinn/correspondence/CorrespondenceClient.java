package no.kommune.bergen.soa.svarut.altinn.correspondence;

import no.altinn.schemas.services.intermediary.receipt._2009._10.ReceiptExternal;
import no.altinn.schemas.services.intermediary.receipt._2009._10.ReceiptStatusEnum;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.ExternalContentV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.InsertCorrespondenceV2;
import no.altinn.schemas.services.serviceengine.notification._2009._10.NotificationBEList;
import no.altinn.services.common.fault._2009._10.AltinnFault;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage;
import no.kommune.bergen.soa.svarut.altinn.AltinnException;
import no.kommune.bergen.soa.svarut.altinn.CdataWriterInterceptor;
import no.kommune.bergen.soa.svarut.altinn.MessageNotification;
import no.kommune.bergen.soa.util.XMLDatatypeUtil;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Altinn Correspondence service client
 */
public class CorrespondenceClient {

	private static final Logger log = LoggerFactory.getLogger(CorrespondenceClient.class);

	private final CorrespondenceSettings settings;

	public CorrespondenceClient(CorrespondenceSettings settings) {
		this.settings = settings;
	}

	public int send(CorrespondenceMessage msg) {
		ICorrespondenceAgencyExternalBasic port = createClientPort();
		InsertCorrespondenceV2 request = createRequest(msg);
		request.setContent(createContent(msg));
		return submitRequest(port, request, msg);
	}

	private NotificationBEList createMessageNotification() {
		MessageNotification messageNotification = new MessageNotification(settings);
		return messageNotification.getNotifications();
	}

	private InsertCorrespondenceV2 createRequest(CorrespondenceMessage msg) {
		InsertCorrespondenceV2 insertCorrespondence = new InsertCorrespondenceV2();
		insertCorrespondence.setServiceCode(settings.getServiceCode());
		insertCorrespondence.setServiceEdition(settings.getServiceEdition());
		insertCorrespondence.setAllowForwarding(false);
		insertCorrespondence.setReportee(msg.getOrgNr());
		insertCorrespondence.setVisibleDateTime(XMLDatatypeUtil.toXMLGregorianCalendar(Calendar.getInstance()));
		insertCorrespondence.setNotifications(createMessageNotification());
		return insertCorrespondence;
	}

	private ExternalContentV2 createContent(CorrespondenceMessage msg) {
		ExternalContentV2 externalContent = new ExternalContentV2();
		externalContent.setLanguageCode(settings.getLanguageCode());
		externalContent.setMessageTitle(msg.getMessageTitle());
		externalContent.setMessageBody(msg.getMessageBody());
		externalContent.setMessageSummary(msg.getMessageSummary());
		return externalContent;
	}

	protected ICorrespondenceAgencyExternalBasic createClientPort() {
		ICorrespondenceAgencyExternalBasic service;
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.getOutInterceptors().add(new CdataWriterInterceptor());

		factory.setServiceClass(ICorrespondenceAgencyExternalBasic.class);
		factory.setAddress(settings.getEndpoint());
		service = (ICorrespondenceAgencyExternalBasic) factory.create();
		if ("true".equalsIgnoreCase(System.getProperty("proxySet"))) {
			Client client = ClientProxy.getClient(service);
			HTTPConduit http = (HTTPConduit) client.getConduit();

			HTTPClientPolicy httpClientPolicy = http.getClient();
			httpClientPolicy.setProxyServer(System.getProperty("http.proxyHost"));
			httpClientPolicy.setProxyServerPort(Integer.parseInt(System.getProperty("http.proxyPort")));
			httpClientPolicy.setNonProxyHosts(System.getProperty("http.nonProxyHosts"));
		}
		return service;
	}

	protected int submitRequest(ICorrespondenceAgencyExternalBasic port, InsertCorrespondenceV2 request, CorrespondenceMessage msg) {

		ReceiptExternal response;
		try {
			response = port.insertCorrespondenceBasicV2(settings.getSystemUserName(), settings.getSystemPassword(), settings.getSystemUserCode(), msg.getExternalReference(), request);
			if (!ReceiptStatusEnum.OK.equals(response.getReceiptStatusCode()))
				throw new AltinnException("Status was not ok: receiptID " + response.getReceiptId() + " status " + response.getReceiptStatusCode());

		} catch (ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage e) {
			throw new AltinnException(getAltinnFaultMessage(e), e);
		}
		return response.getReceiptId();
	}

	private String getAltinnFaultMessage(ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicV2AltinnFaultFaultFaultMessage f) {
		AltinnFault faultMessage = f.getFaultInfo();
		log.info("Error id:" + faultMessage.getErrorID() + "Error: " + faultMessage.getAltinnErrorMessage());
		return faultMessage.getAltinnErrorMessage();
	}

	public CorrespondenceSettings getSettings() {
		return settings;
	}
}
