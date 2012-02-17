package no.kommune.bergen.soa.common.jms;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/** Wrapper for Spring jms */
public class Jms {
	static final Logger logger = Logger.getLogger( Jms.class );
	private JmsTemplate jmsTemplate = null;;
	private Queue readQueue = null;
	private Queue writeQueue = null;
	private Queue errorQueue = null;
	private Queue deadLetterQueue = null;
	private QueueConnectionFactory queueConnectionFactory = null;

	public void sendResponse( Message messageReceived, String response ) throws JMSException {
		Jms.sendResponse( jmsTemplate, messageReceived, response );
	}

	public String sendToWriteQueueWaitForResponse( final String msg ) throws JMSException {
		return Jms.sendWaitForResponse( queueConnectionFactory, writeQueue, msg );
	}

	public Message receiveFromReadQueue() throws JMSException {
		return Jms.receiveMessageFromQueue( jmsTemplate, readQueue );
	}

	public Message receiveSelectedFromReadQueue( String messageSelector ) throws JMSException {
		return Jms.receiveSelectedMessageFromQueue( jmsTemplate, readQueue, messageSelector );
	}

	public String receiveTextMessageFromReadQueue() throws JMSException {
		return Jms.receiveTextMessageFromQueue( jmsTemplate, readQueue );
	}

	public void sendToWriteQueue( final Message msg ) {
		Jms.send( this.jmsTemplate, writeQueue, msg );
	}

	public void sendToWriteQueue( final String msg ) {
		Jms.send( this.jmsTemplate, writeQueue, msg );
	}

	public void sendToWriteQueue( final byte[] msg ) {
		Jms.send( this.jmsTemplate, writeQueue, msg );
	}

	public void sendToErrorQueue( final Message msg ) {
		Jms.send( this.jmsTemplate, errorQueue, msg );
	}

	public void sendToErrorQueue( final String msg ) {
		Jms.send( this.jmsTemplate, errorQueue, msg );
	}

	public void sendToErrorQueue( final byte[] msg ) {
		Jms.send( this.jmsTemplate, errorQueue, msg );
	}

	public void sendToDeadLetterQueue( final Message msg ) {
		Jms.send( this.jmsTemplate, deadLetterQueue, msg );
	}

	public void sendToDeadLetterQueue( final String msg ) {
		Jms.send( this.jmsTemplate, deadLetterQueue, msg );
	}

	public void sendToDeadLetterQueue( final byte[] msg ) {
		Jms.send( this.jmsTemplate, deadLetterQueue, msg );
	}

	private static void send( final JmsTemplate jmsTemplate, final Queue queue, final String msg ) {
		send( jmsTemplate, queue, new MessageCreator() {
			@Override
			public Message createMessage( Session session ) throws JMSException {
				if (logger.isDebugEnabled()) logger.debug( "Creating JMS message " + msg );
				return session.createTextMessage( msg );
			}
		} );
	}

	private static void sendResponse( final JmsTemplate jmsTemplate, final Message messageReceived, final String response ) throws JMSException {
		logger.debug( "sendResponse() start " );
		Destination replyTo = messageReceived.getJMSReplyTo();
		if (replyTo == null) {
			logger.debug( "sendResponse() end - NO response sent" );
			return;
		}
		jmsTemplate.send( replyTo, new MessageCreator() {
			@Override
			public Message createMessage( Session session ) throws JMSException {
				TextMessage message = session.createTextMessage( response );
				String correlationId = messageReceived.getJMSMessageID();
				message.setJMSCorrelationID( correlationId );
				if (logger.isDebugEnabled()) logger.debug( String.format( "Creating JMS message - JMSCorrelationID = '%s'", correlationId ) );
				return message;
			}
		} );
		logger.debug( "sendResponse() end - response sent" );
	}

	private static void send( final JmsTemplate jmsTemplate, final Queue queue, final byte[] msg ) {
		send( jmsTemplate, queue, new MessageCreator() {
			@Override
			public Message createMessage( Session session ) throws JMSException {
				if (logger.isDebugEnabled()) logger.debug( "Creating JMS message " + msg );
				BytesMessage message = session.createBytesMessage();
				message.writeBytes( msg );
				return message;
			}
		} );
	}

	private static String sendWaitForResponse( final QueueConnectionFactory connectionFactory, final Queue writeQueue, final String msg ) throws JMSException {
		QueueConnection connection = null;
		QueueSession session = null;
		QueueSender sender = null;
		QueueReceiver receiver = null;
		logger.debug( "sendWaitForResponse() start" );
		try {
			connection = connectionFactory.createQueueConnection();
			connection.start();
			session = connection.createQueueSession( false, Session.AUTO_ACKNOWLEDGE );
			TextMessage message = session.createTextMessage();
			message.setText( msg );
			message.setJMSDestination( writeQueue );
			TemporaryQueue temporaryQueue = session.createTemporaryQueue();
			message.setJMSReplyTo( temporaryQueue );
			sender = session.createSender( writeQueue );
			sender.send( message );
			String messageSelector = String.format( "JMSCorrelationID = '%s'", message.getJMSMessageID() );
			receiver = session.createReceiver( temporaryQueue, messageSelector );
			if (logger.isDebugEnabled()) logger.debug( "sendWaitForResponse() - Waiting for response messageSelector=" + messageSelector );
			Message responseMessage = receiver.receive( 5000 );
			if (responseMessage != null && responseMessage instanceof TextMessage) {
				logger.debug( "sendWaitForResponse() - response received" );
				return ((TextMessage) responseMessage).getText();
			}
		} finally {
			if (connection != null) connection.close();
			if (session != null) session.close();
			if (sender != null) sender.close();
			if (receiver != null) receiver.close();
		}
		logger.debug( "sendWaitForResponse() - no response found" );
		return null;
	}

	private static void send( final JmsTemplate jmsTemplate, final Queue queue, final Message msg ) {
		send( jmsTemplate, queue, new MessageCreator() {
			@Override
			public Message createMessage( Session session ) throws JMSException {
				return msg;
			}
		} );
	}

	private static void send( final JmsTemplate jmsTemplate, final Queue queue, final MessageCreator messageCreator ) {
		if (messageCreator == null || queue == null || jmsTemplate == null) {
			if (logger.isDebugEnabled()) logger.debug( "Nothing sent. MessageCreator or JmsTemplate or Queue is null. " );
			return;
		}
		jmsTemplate.send( queue, messageCreator );
		if (logger.isDebugEnabled()) {
			String queueName = "unknown";
			try {
				queueName = queue.getQueueName();
			} catch (JMSException e) {
				logger.debug( "QueuegetQueueName failed!", e.getCause() );
			}
			logger.debug( "Sent message to desitnation " + queueName );
		}
	}

	private static Message receiveMessageFromQueue( JmsTemplate jmsTemplate, Queue queue ) throws JMSException {
		if (jmsTemplate == null || queue == null) return null;
		return jmsTemplate.receive( queue );
	}

	private static Message receiveSelectedMessageFromQueue( JmsTemplate jmsTemplate, Queue queue, String messageSelector ) throws JMSException {
		if (jmsTemplate == null || queue == null) return null;
		return jmsTemplate.receiveSelected( queue, messageSelector );
	}

	private static String receiveTextMessageFromQueue( JmsTemplate jmsTemplate, Queue queue ) throws JMSException {
		Message msg = null;
		msg = receiveMessageFromQueue( jmsTemplate, queue );
		if (msg == null) return null;
		return ((TextMessage) msg).getText();
	}

	public void setJmsTemplate( JmsTemplate jmsTemplate ) {
		this.jmsTemplate = jmsTemplate;
	}

	public void setReadQueue( Queue readQueue ) {
		this.readQueue = readQueue;
	}

	public void setWriteQueue( Queue writeQueue ) {
		this.writeQueue = writeQueue;
	}

	public void setErrorQueue( Queue errorQueue ) {
		this.errorQueue = errorQueue;
	}

	public void setDeadLetterQueue( Queue deadLetterQueue ) {
		this.deadLetterQueue = deadLetterQueue;
	}

	public QueueConnectionFactory getQueueConnectionFactory() {
		return queueConnectionFactory;
	}

	public void setQueueConnectionFactory( QueueConnectionFactory queueConnectionFactory ) {
		this.queueConnectionFactory = queueConnectionFactory;
		this.jmsTemplate = new JmsTemplate( this.queueConnectionFactory );
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public Queue getReadQueue() {
		return readQueue;
	}

	public Queue getWriteQueue() {
		return writeQueue;
	}

	public Queue getErrorQueue() {
		return errorQueue;
	}

	public Queue getDeadLetterQueue() {
		return deadLetterQueue;
	}

}
