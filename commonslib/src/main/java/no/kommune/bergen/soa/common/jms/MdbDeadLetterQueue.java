package no.kommune.bergen.soa.common.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message Driven Bean . Upon onMessage() invocation, Will attempt to invoke serviceInvoker.invoke(message). Messages that cause
 * serviceInvoker.invoke(message) to throw exceptions will be forwarded to jms.deadLetterQueue.
 * @author jarle.borsheim
 */
public class MdbDeadLetterQueue implements MessageListener, ExceptionListener {
	static final Logger logger = Logger.getLogger( MdbDeadLetterQueue.class );
	static org.slf4j.Logger failedLog = LoggerFactory.getLogger( "failedLog" );
	/** The actual executing business service. */
	private ServiceInvoker serviceInvoker = null;;
	private Jms jms = null;;

	public MdbDeadLetterQueue( Jms jms, ServiceInvoker serviceInvoker ) {
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
		} catch (Exception e) {
			logger.error( "Message cannot be processed. Sending message back to dead letter queue and log to failedLog (used by oppsyn): " + message, e );
			failedLog.error("Error Message has failed to be prosessed. Check logs. Message:" + message);
			jms.sendToDeadLetterQueue( message );
		}
	}

	@Override
	public void onException( JMSException e ) {
		logger.error( "onException() was called!", e );
	}

}
