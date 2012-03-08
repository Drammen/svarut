package no.kommune.bergen.soa.svarut.altin;

import java.util.Calendar;

import no.altinn.schemas.serviceengine.formsengine._2009._10.TransportType;
import no.altinn.schemas.services.serviceengine.notification._2009._10.*;
import no.kommune.bergen.soa.util.XMLDatatypeUtil;

/** CorrespondenceClient helper */
public class MessageNotification {
	final NotificationBEList notifications = new NotificationBEList();
	final Notification notification = new Notification();
	final ReceiverEndPointBEList endPoints = new ReceiverEndPointBEList();

	public MessageNotification( CorrespondenceSettings settings ) {
		this.notification.setFromAddress( settings.getFromAddress() );
		this.notification.setLanguageCode( settings.getLanguageCode() );
		this.notification.setNotificationType( settings.getNotificationType() );
		this.notification.setShipmentDateTime( XMLDatatypeUtil.toXMLGregorianCalendar( Calendar.getInstance() ) );
		add(TransportType.SMS, "");
		add(TransportType.EMAIL, "");
		this.notification.setReceiverEndPoints( endPoints );
		this.notifications.getNotification().add( notification );


	}

	private void add( TransportType transportType, String receiverAddress ) {
		ReceiverEndPoint receiverEndPoint = new ReceiverEndPoint();
		receiverEndPoint.setReceiverAddress( "" );
		receiverEndPoint.setTransportType( transportType );
		endPoints.getReceiverEndPoint().add( receiverEndPoint );
	}
	public NotificationBEList getNotifications() {
		return notifications;
	}
}
