package no.kommune.bergen.soa.common.jms;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class JmsTest {
	private JmsTemplate jmsTemplate = null;;
	private Queue readQueue;
	private Queue writeQueue;
	private Queue errorQueue;
	private Queue deadLetterQueue;
	private Jms jms;

	@Before
	public void setup() {
		jmsTemplate = createStrictMock( JmsTemplate.class );
		readQueue = createStrictMock( Queue.class );
		writeQueue = createStrictMock( Queue.class );
		errorQueue = createStrictMock( Queue.class );
		deadLetterQueue = createStrictMock( Queue.class );
		jms = new Jms();
		jms.setJmsTemplate( jmsTemplate );
		jms.setDeadLetterQueue( deadLetterQueue );
		jms.setErrorQueue( errorQueue );
		jms.setReadQueue( readQueue );
		jms.setWriteQueue( writeQueue );
	}

	@Test
	public void receiveTextMessageFromReadQueue() throws Exception {
		TextMessage message = createStrictMock( TextMessage.class );
		expect( jmsTemplate.receive( readQueue ) ).andReturn( message );
		replay( jmsTemplate );
		jms.receiveTextMessageFromReadQueue();
		verify( jmsTemplate );
	}

	@Test
	public void receiveFromReadQueue() throws Exception {
		expect( jmsTemplate.receive( readQueue ) ).andReturn( createStrictMock( Message.class ) );
		replay( jmsTemplate );
		jms.receiveFromReadQueue();
		verify( jmsTemplate );
	}

	@Test
	public void receiveFromReadQueueEmptyQueue() throws Exception {
		expect( jmsTemplate.receive( readQueue ) ).andReturn( null );
		replay( jmsTemplate );
		assertNull( jms.receiveFromReadQueue() );
		verify( jmsTemplate );
	}

	@Test
	public void receiveFromReadQueueAndThrowException() throws Exception {
		final String ErrorMessage = "Failed to receive from queue deliberately";
		expect( jmsTemplate.receive( readQueue ) ).andThrow( new RuntimeException( ErrorMessage ) );
		replay( jmsTemplate );
		try {
			jms.receiveFromReadQueue();
			fail( "Should not be reached!" );
		} catch (Exception e) {
			assertEquals( ErrorMessage, e.getMessage() );
		}
		verify( jmsTemplate );
	}

	@Test
	public void receiveFromReadQueueButQueueIsNull() throws Exception {
		jms.setReadQueue( null );
		assertNull( jms.receiveFromReadQueue() );
	}

	@Test
	public void receiveFromReadQueueButJmsTemplateIsNull() throws Exception {
		jms.setJmsTemplate( null );
		assertNull( jms.receiveFromReadQueue() );
	}

	@Test
	public void sendToWriteQueue() throws Exception {
		jmsTemplate.send( same( writeQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToWriteQueue( createStrictMock( Message.class ) );
		verify( jmsTemplate );
	}

	@Test
	public void sendStringToWriteQueue() throws Exception {
		jmsTemplate.send( same( writeQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToWriteQueue( "Message" );
		verify( jmsTemplate );
	}

	@Test
	public void sendBytesToWriteQueue() throws Exception {
		jmsTemplate.send( same( writeQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToWriteQueue( "Message".getBytes() );
		verify( jmsTemplate );
	}

	@Test
	public void sendToErrorQueue() throws Exception {
		jmsTemplate.send( same( errorQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToErrorQueue( createStrictMock( Message.class ) );
		verify( jmsTemplate );
	}

	@Test
	public void sendStringToErrorQueue() throws Exception {
		jmsTemplate.send( same( errorQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToErrorQueue( "Error" );
		verify( jmsTemplate );
	}

	@Test
	public void sendBytesToErrorQueue() throws Exception {
		jmsTemplate.send( same( errorQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToErrorQueue( "Error".getBytes() );
		verify( jmsTemplate );
	}

	@Test
	public void sendToDeadLetterQueue() throws Exception {
		jmsTemplate.send( same( deadLetterQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToDeadLetterQueue( createStrictMock( Message.class ) );
		verify( jmsTemplate );
	}

	@Test
	public void sendStringToDeadLetterQueue() throws Exception {
		jmsTemplate.send( same( deadLetterQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToDeadLetterQueue( "PoisonMessage" );
		verify( jmsTemplate );
	}

	@Test
	public void sendBytesToDeadLetterQueue() throws Exception {
		jmsTemplate.send( same( deadLetterQueue ), isA( MessageCreator.class ) );
		replay( jmsTemplate );
		jms.sendToDeadLetterQueue( "PoisonMessage".getBytes() );
		verify( jmsTemplate );
	}

	@Test
	public void sendAndThrowException() throws Exception {
		final String ErrorMessage = "Failed send deliberately";
		jmsTemplate.send( same( deadLetterQueue ), isA( MessageCreator.class ) );
		expectLastCall().andThrow( new RuntimeException( ErrorMessage ) );
		replay( jmsTemplate );
		try {
			jms.sendToDeadLetterQueue( createStrictMock( Message.class ) );
			fail( "Should not be reached!" );
		} catch (Exception e) {
			assertEquals( ErrorMessage, e.getMessage() );
		}
		verify( jmsTemplate );
	}

	@Test
	public void sendJmsTemplateIsNull() throws Exception {
		jms.setJmsTemplate( null );
		jms.sendToDeadLetterQueue( "NotToBeSentMessage" );
	}

	@Test
	public void sendQueueIsNull() throws Exception {
		jms.setDeadLetterQueue( null );
		jms.sendToDeadLetterQueue( "NotToBeSentMessage" );
	}

	@Test
	public void sendMessageIsNull() throws Exception {
		jms.sendToDeadLetterQueue( (Message)null );
	}

}
