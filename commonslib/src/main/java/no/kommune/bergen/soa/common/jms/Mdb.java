package no.kommune.bergen.soa.common.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

/**
 * Message Driven Bean. Upon onMessage() invocation, Will attempt to invoke serviceInvoker.invoke(message). Messages that cause
 * serviceInvoker.invoke(message) to throw exceptions will be forwarded; -- PoisonMessageException to jms.deadLetterQueue, -- all
 * other exceptions to jms.errorQueue.
 */
public class Mdb implements MessageListener, ExceptionListener {
	static final Logger logger = Logger.getLogger( Mdb.class );
	/** The actual executing business service. */
	private ServiceInvoker serviceInvoker = null;;
	private Jms jms = null;;

	public Mdb( Jms jms, ServiceInvoker serviceInvoker ) {
		this.serviceInvoker = serviceInvoker;
		this.jms = jms;
	}

	/**
	 * Message is automatically acknowledged upon successful completion of this method
	 *
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage( Message message ) {
		try {
			serviceInvoker.invoke( message );
		} catch (PoisonMessageException e) {
			logger.error( "Message cannot be processed. Forwarding message to dead letter queue: " + message, e );
			jms.sendToDeadLetterQueue( message );
		} catch (Exception e) {
			logger.error( "Failed to process message. Forwarding message to error queue: " + message, e );
			jms.sendToErrorQueue( message );
		}
	}

	@Override
	public void onException( JMSException e ) {
		logger.error( "onException() was called!", e );
	}

}
