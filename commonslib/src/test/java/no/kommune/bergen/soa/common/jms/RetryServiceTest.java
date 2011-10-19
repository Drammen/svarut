package no.kommune.bergen.soa.common.jms;

import static org.easymock.EasyMock.contains;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import javax.jms.Message;

import org.junit.Before;
import org.junit.Test;

public class RetryServiceTest {
	private static final String retryCounterPropertyName = "RetryCounter";
	private Jms mockJms;
	private RetryService retryService;
	private Message mockMessage;

	@Before
	public void setup() {
		mockJms = createStrictMock( Jms.class );
		mockMessage = createStrictMock( Message.class );
		retryService = new RetryService( mockJms );
		retryService.setMaxRetries( 3 );
	}

	@Test
	public void serviceNonEmptyReadQueue() throws Exception {
		expect( mockJms.receiveSelectedFromReadQueue( contains( "JMSTimestamp" ) ) ).andReturn( mockMessage );
		expect( mockMessage.propertyExists( retryCounterPropertyName ) ).andReturn( false );
		mockMessage.clearProperties();
		mockMessage.setIntProperty( retryCounterPropertyName, 1 );
		mockJms.sendToWriteQueue( mockMessage );
		expect( mockJms.receiveSelectedFromReadQueue( contains( "JMSTimestamp" ) ) ).andReturn( null );
		replayAndVerify();

	}

	@Test
	public void serviceExhaustedRetryCounter() throws Exception {
		expect( mockJms.receiveSelectedFromReadQueue( contains( "JMSTimestamp" ) ) ).andReturn( mockMessage );
		expect( mockMessage.propertyExists( retryCounterPropertyName ) ).andReturn( true );
		expect( mockMessage.getIntProperty( retryCounterPropertyName ) ).andReturn( 3 );
		mockJms.sendToErrorQueue( mockMessage );
		expect( mockJms.receiveSelectedFromReadQueue( contains( "JMSTimestamp" ) ) ).andReturn( null );
		replayAndVerify();
	}

	@Test
	public void serviceExceptionThrown() throws Exception {
		expect( mockJms.receiveSelectedFromReadQueue( contains( "JMSTimestamp" ) ) ).andReturn( mockMessage );
		expect( mockMessage.propertyExists( retryCounterPropertyName ) ).andThrow( new RuntimeException( "TESTING" ) );
		mockJms.sendToErrorQueue( mockMessage );
		expect( mockJms.receiveSelectedFromReadQueue( contains( "JMSTimestamp" ) ) ).andReturn( null );
		replayAndVerify();
	}

	private void replayAndVerify() throws Exception {
		replay( mockMessage );
		replay( mockJms );
		retryService.service();
		verify( mockJms );
		verify( mockMessage );
	}

}
