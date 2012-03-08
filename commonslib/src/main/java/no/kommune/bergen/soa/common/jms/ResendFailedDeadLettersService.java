package no.kommune.bergen.soa.common.jms;

import javax.jms.Message;

import no.kommune.bergen.soa.common.job.JobCommand;

import org.apache.log4j.Logger;

/**
 * Service to move failed messages in the failedDeadLetterQueue back to the deadLetterQueue.
 * @author jarle.borsheim
 */
public class ResendFailedDeadLettersService implements JobCommand {
	private static Logger logger = Logger.getLogger( ResendFailedDeadLettersService.class );
	private final Jms jms;

	public ResendFailedDeadLettersService( Jms jms ) {
		this.jms = jms;
	}

	/**
	 * Service entry point. Will read the failedDeadLetterQueue until exhausted and put them into the deadLetterQueue.
	 * If failing to do so it will put the message back into it's own queue.
	 * @throws Exception
	 */
	@Override
	public void service() throws Exception {
		if (logger.isDebugEnabled()) logger.debug( "service start." );
		while (true) {
			Message message = null;
			message = jms.receiveFromReadQueue();
			if (message == null) return;
			try {
				move( message );
			} catch (RuntimeException e) {
				logger.warn( "ResendFailedDeadLettersService.service() failed. Putting the message back on the failedDeadLetterQueue. Please check the mailsending properties and verify that mailsending works from server.", e );
				jms.sendToErrorQueue( message );
				if (logger.isDebugEnabled()) logger.debug( "Put back on the failedDeadLetterQueue: " + message );
			}
		}
	}

	/** Dispatch message for retry if set maximum number of attempts are not exhausted */
	private void move( Message message ) {
		if (logger.isInfoEnabled()) logger.info( "Message will be moved to deadLetterQueue for resending of message: " + message );
		jms.sendToDeadLetterQueue( message );
	}
}
