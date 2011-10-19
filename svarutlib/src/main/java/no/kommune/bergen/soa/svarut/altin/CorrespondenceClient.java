package no.kommune.bergen.soa.svarut.altin;

import java.util.Calendar;

import no.altinn.schemas.services.intermediary.receipt._2009._10.ReceiptExternal;
import no.altinn.schemas.services.serviceengine.correspondence._2009._10.ExternalContent;
import no.altinn.schemas.services.serviceengine.correspondence._2009._10.InsertCorrespondence;
import no.altinn.schemas.services.serviceengine.notification._2009._10.NotificationBEList;
import no.altinn.services.common.fault._2009._10.AltinnFault;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicAltinnFaultFaultFaultMessage;
import no.kommune.bergen.soa.util.XMLDatatypeUtil;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

/** Altinn Correspondence service client */
public class CorrespondenceClient {
	private final CorrespondenceSettings settings;

	public CorrespondenceClient( CorrespondenceSettings settings ) {
		this.settings = settings;
	}

	public int send( CorrespondenceMessage msg ) {
		ICorrespondenceAgencyExternalBasic port = createClientPort();
		InsertCorrespondence request = createRequest( msg );
		request.setContent( createContent( msg ) );
		return submitRequest( port, request, msg );
	}

	private NotificationBEList createMessageNotification( CorrespondenceMessage msg ) {
		MessageNotification messageNotification = new MessageNotification( this.settings );
		String sms = msg.getSmsToNotify();
		String email = msg.getEmailToNotify();
		if (isOmitted( sms ) && isOmitted( email )) return null;
		messageNotification.addEmail( email );
		messageNotification.addSms( sms );
		return messageNotification.notifications;
	}

	private InsertCorrespondence createRequest( CorrespondenceMessage msg ) {
		InsertCorrespondence insertCorrespondence = new InsertCorrespondence();
		insertCorrespondence.setServiceCode( this.settings.getServiceCode() );
		insertCorrespondence.setServiceEdition( this.settings.getServiceEdition() );
		insertCorrespondence.setReportee( msg.getOrgNr() );
		insertCorrespondence.setVisibleDateTime( XMLDatatypeUtil.toXMLGregorianCalendar( Calendar.getInstance() ) );
		insertCorrespondence.setNotifications( createMessageNotification( msg ) );
		return insertCorrespondence;
	}

	private ExternalContent createContent( CorrespondenceMessage msg ) {
		ExternalContent externalContent = new ExternalContent();
		externalContent.setLanguageCode( this.settings.getLanguageCode() );
		externalContent.setMessageTitle( msg.getMessageTitle() );
		externalContent.setMessageBody( msg.getMessageBody() );
		externalContent.setMessageSummary( msg.getMessageSummary() );
		return externalContent;
	}

	protected ICorrespondenceAgencyExternalBasic createClientPort() {
		ICorrespondenceAgencyExternalBasic service;
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass( ICorrespondenceAgencyExternalBasic.class );
		factory.setAddress( this.settings.getEndpoint() );
		service = (ICorrespondenceAgencyExternalBasic) factory.create();
		if ("true".equalsIgnoreCase( System.getProperty( "proxySet" ) )) {
			Client client = ClientProxy.getClient( service );
			HTTPConduit http = (HTTPConduit) client.getConduit();

			HTTPClientPolicy httpClientPolicy = http.getClient();
			httpClientPolicy.setProxyServer( System.getProperty( "http.proxyHost" ) );
			httpClientPolicy.setProxyServerPort( Integer.parseInt( System.getProperty( "http.proxyPort" ) ) );
		}
		return service;
	}

	protected int submitRequest( ICorrespondenceAgencyExternalBasic port, InsertCorrespondence request, CorrespondenceMessage msg ) {

		ReceiptExternal response = null;
		try {
			response = port.insertCorrespondenceBasic( this.settings.getSystemUserName(), this.settings.getSystemPassword(), this.settings.getSystemUserCode(), msg.getExternalReference(), request );
		} catch (ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicAltinnFaultFaultFaultMessage e) {
			throw new RuntimeException( getAltinFaultMessage( e ), e );
		}
		return response.getReceiptId();
	}

	private String getAltinFaultMessage( ICorrespondenceAgencyExternalBasicInsertCorrespondenceBasicAltinnFaultFaultFaultMessage f ) {
		AltinnFault faultMessage = f.getFaultInfo();
		return faultMessage.getAltinnErrorMessage();
	}

	private boolean isOmitted( String str ) {
		return str == null || str.trim().isEmpty();
	}

	public CorrespondenceSettings getSettings() {
		return settings;
	}

}
