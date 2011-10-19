package no.kommune.bergen.soa.common.jms;

import javax.jms.Message;

import no.kommune.bergen.soa.common.job.JobCommand;

import org.apache.log4j.Logger;

/**
 * Service to retry processing of a message that failed. When processing of a message is unsuccessful, The message is enqueued for
 * another attempt later. This service reads messages from a jms queue. Could be invoked as a quarz job at intervals. Will attempt
 * to read from jms.readQueue and redirect to jms.writeQueue if retry is adviced else to jms.errorQueue.
 *
 *
 * @author einar
 */
public class RetryService implements JobCommand {
	private static Logger logger = Logger.getLogger( RetryService.class );
	private final Jms jms;
	/** Maximum number of attempts for successful SKjema processing. */
	private int maxRetries = 3;
	private static final String retryCounterPropertyName = "RetryCounter";

	public RetryService( Jms jms ) {
		this.jms = jms;
	}

	/**
	 * Service entry point. Will read the retryQueue until exhausted and dispatch forms for another attempt if more attempts are
	 * permitted. Otherwise send to Dead Letter Queue.
	 *
	 * @throws Exception
	 */
	public void service() throws Exception {
		if (logger.isDebugEnabled()) logger.debug( "service start." );
		String messageSelector = "JMSTimestamp < " + System.currentTimeMillis();
		while (true) {
			Message message = null;
			message = jms.receiveSelectedFromReadQueue( messageSelector );
			if (message == null) return;
			try {
				retry( message );
			} catch (RuntimeException e) {
				logger.warn( "RetryQueueHandler.service() failed", e );
				jms.sendToErrorQueue( message );
				if (logger.isDebugEnabled()) logger.debug( "sentTo deadLetterQueue: " + message );
				//				}
			}
		}
	}

	/** Dispatch message for retry if set maximum number of attempts are not exhausted */
	private void retry( Message message ) {
		if (logger.isDebugEnabled()) logger.debug( "Will examine msg for retry. Msg : " + message );
		if (isRetryOkThenIncrementCounter( message )) {
			if (logger.isInfoEnabled()) logger.info( "Message will be retried: " + message );
			jms.sendToWriteQueue( message );
		} else {
			if (logger.isInfoEnabled()) logger.info( "Message will NOT be retried: " + message );
			jms.sendToErrorQueue( message );
		}
	}

	/** Verify if another attempt is permitted and if so, increment retry counter. */
	private boolean isRetryOkThenIncrementCounter( Message message ) {
		try {
			int count = 0;
			if (message.propertyExists( this.retryCounterPropertyName )) {
				count = message.getIntProperty( this.retryCounterPropertyName );
			}
			boolean isRetryOk = count < maxRetries;
			if (logger.isDebugEnabled())
				logger.debug( "isRetryOk: " + isRetryOk + " retry cont: " + count + " max retries: " + maxRetries );
			if (isRetryOk) {
				count++;
				logger.debug( this.retryCounterPropertyName + ":" + count );
				message.clearProperties();
				message.setIntProperty( this.retryCounterPropertyName, count );
			}
			return isRetryOk;
		} catch (Exception e) {
			throw new RuntimeException( "Poisonous Message!", e );
		}
	}

	/** Maximum number of times the message can be retried before forward to dead letter queue */
	public void setMaxRetries( int maxRetries ) {
		this.maxRetries = maxRetries;
	}

}
