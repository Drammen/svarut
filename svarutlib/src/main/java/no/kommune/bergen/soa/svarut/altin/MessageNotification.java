package no.kommune.bergen.soa.svarut.altin;

import java.util.Calendar;

import no.altinn.schemas.serviceengine.formsengine._2009._10.TransportType;
import no.altinn.schemas.services.serviceengine.notification._2009._10.Notification;
import no.altinn.schemas.services.serviceengine.notification._2009._10.NotificationBEList;
import no.altinn.schemas.services.serviceengine.notification._2009._10.ReceiverEndPoint;
import no.altinn.schemas.services.serviceengine.notification._2009._10.ReceiverEndPointBEList;
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
		this.notification.setReceiverEndPoints( endPoints );
		this.notifications.getNotification().add( notification );
	}

	public void addSms( String receiverSmsNumber ) {
		add( TransportType.SMS, receiverSmsNumber );
	}

	public void addEmail( String receiverEmail ) {
		add( TransportType.EMAIL, receiverEmail );
	}

	private void add( TransportType transportType, String receiverAddress ) {
		if (isOmitted( receiverAddress )) return;
		ReceiverEndPoint receiverEndPoint = new ReceiverEndPoint();
		receiverEndPoint.setReceiverAddress( receiverAddress );
		receiverEndPoint.setTransportType( transportType );
		endPoints.getReceiverEndPoint().add( receiverEndPoint );
	}

	private boolean isOmitted( String str ) {
		return str == null || str.trim().isEmpty();
	}

	public NotificationBEList getNotifications() {
		return notifications;
	}
}
