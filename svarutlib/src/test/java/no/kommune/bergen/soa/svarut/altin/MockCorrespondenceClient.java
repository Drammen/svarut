package no.kommune.bergen.soa.svarut.altin;

import junit.framework.Assert;
import no.altinn.schemas.serviceengine.formsengine._2009._10.TransportType;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.ExternalContentV2;
import no.altinn.schemas.services.serviceengine.correspondence._2010._10.InsertCorrespondenceV2;
import no.altinn.schemas.services.serviceengine.notification._2009._10.Notification;
import no.altinn.schemas.services.serviceengine.notification._2009._10.ReceiverEndPoint;
import no.altinn.services.serviceengine.correspondence._2009._10.ICorrespondenceAgencyExternalBasic;

import java.util.List;

public class MockCorrespondenceClient extends CorrespondenceClient {
	private final CorrespondenceSettings settings;
	private CorrespondenceMessage latestMessage = null;

	public MockCorrespondenceClient( CorrespondenceSettings settings ) {
		super( settings );
		this.settings = settings;
	}

	@Override
	protected ICorrespondenceAgencyExternalBasic createClientPort() {
		return null;
	}

	@Override
	protected int submitRequest( ICorrespondenceAgencyExternalBasic port, InsertCorrespondenceV2 request, CorrespondenceMessage message ) {
		Assert.assertEquals( settings.getServiceCode(), request.getServiceCode() );
		Assert.assertEquals( settings.getServiceEdition(), request.getServiceEdition() );
		Assert.assertEquals( message.getOrgNr(), request.getReportee() );
		ExternalContentV2 content = request.getContent();
		Assert.assertEquals( message.getMessageTitle(), content.getMessageTitle() );
		Assert.assertEquals( message.getMessageBody(), content.getMessageBody() );
		Assert.assertEquals( message.getMessageSummary(), content.getMessageSummary() );
		Assert.assertEquals( settings.getLanguageCode(), content.getLanguageCode() );
		Notification notification = request.getNotifications().getNotification().get( 0 );
		List<ReceiverEndPoint> receiverEndPoints = notification.getReceiverEndPoints().getReceiverEndPoint();
		for (ReceiverEndPoint receiverEndPoint : receiverEndPoints) {
			TransportType transportType = receiverEndPoint.getTransportType();
			if (TransportType.EMAIL.equals( transportType )) {
				Assert.assertEquals( message.getEmailToNotify(), receiverEndPoint.getReceiverAddress() );
			} else if (TransportType.SMS.equals( transportType )) {
				Assert.assertEquals( message.getSmsToNotify(), receiverEndPoint.getReceiverAddress() );
			} else {
				Assert.fail( "Unknown TransportType: " + transportType );
			}
		}
		this.latestMessage = message;
		return 1;
	}

	public static CorrespondenceSettings newSettings() {
		CorrespondenceSettings settings = new CorrespondenceSettings();
		settings.setNotificationType( "Melding fra Bergen kommune" );
		settings.setFromAddress( "einarvalen@gmail.com" );
		settings.setLanguageCode( "1044" );
		settings.setServiceCode( "1268" );
		settings.setEndpoint( "https://tt02.altinn.basefarm.net/ServiceEngineExternal/CorrespondenceAgencyExternalBasic.svc" );
		settings.setSystemUserName( "BK_meldinger" );
		settings.setSystemPassword( "UO6pXuIt" );
		settings.setSystemUserCode( "BK_meldinger" );
		settings.setServiceEdition( "1" );
		settings.setBodyTemplate( "<h1>$TITTEL</h1><p>$MELDING</p><p>For å åpne dokumentet, <a href='$Link'> Klikk her. </a>" );
		settings.setSubjectTemplate( "$TITTEL" );
		return settings;
	}

	public static CorrespondenceMessage createMessage() {
		CorrespondenceMessage message = new CorrespondenceMessage();
		message.setOrgNr( "910558919" );
		message.setSmsToNotify( "004795996325" );
		message.setEmailToNotify( "einarvalen@gmail.com" );
		message.setMessageSummary( "TestMessageSummary" );
		message.setMessageTitle( "Meldings tittel" );
		message.setMessageBody( "Meldingens tekst" );
		message.setExternalReference( "forsendelsesId" );
		return message;
	}

	@Override
	public CorrespondenceSettings getSettings() {
		return settings;
	}

	public CorrespondenceMessage getLatestMessage() {
		return latestMessage;
	}

}

/*
https://tt02.altinn.basefarm.net/no/
login: 04017002479
Org: 910558919
Pins: 1: ajhhs, Pin2: piksd, Pin3: iuyhs, Pin4: asdfg, Pin5: rtefs, Pin6: loj7s, Pin7: mmmyp, Pin8: juksa, Pin9: fizck, Pin10: qicks, Pin11: 98ujs, Pin12: mnbvs, Pin13: qwers, Pin14: polze, Pin15: ztang, Pin16: alt1n, Pin17: zcatt, Pin18: kjasd, Pin19: 23as3, Pin20: lkiju, Pin21: 4564s, Pin22: zxhfg, Pin23: alsks, Pin24: ooiks, Pin25: likme, Pin26: kaffe, Pin27: arbei, Pin28: 00kks, Pin29: mjhgg, Pin30: ziste.
*/

