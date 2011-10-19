package no.kommune.bergen.soa.common.jms;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import javax.jms.Message;

import org.junit.Test;

public class MdbTest {

	@Test
	public void onMessage() throws Exception {
		final String invokeCalled = "innvoke called";
		final String[] status = new String[1];
		Jms mockJms = createStrictMock( Jms.class );
		final Message mockMessage = createStrictMock( Message.class );
		Mdb mdb = new Mdb( mockJms, new ServiceInvoker() {
			public void invoke( Message message ) throws Exception {
				status[0] = invokeCalled;
			}
		} );
		mdb.onMessage( mockMessage );
		assertEquals( invokeCalled, status[0] );
	}

	@Test
	public void onMessagePoisonMessageException() throws Exception {
		Jms mockJms = createStrictMock( Jms.class );
		final Message mockMessage = createStrictMock( Message.class );
		Mdb mdb = new Mdb( mockJms, new ServiceInvoker() {
			public void invoke( Message message ) throws Exception {
				throw new PoisonMessageException( "TESTING" );
			}
		} );
		mockJms.sendToDeadLetterQueue( mockMessage );
		expectLastCall();
		replay( mockJms );
		mdb.onMessage( mockMessage );
		verify( mockJms );
	}

	@Test
	public void onMessageException() throws Exception {
		Jms mockJms = createStrictMock( Jms.class );
		final Message mockMessage = createStrictMock( Message.class );
		Mdb mdb = new Mdb( mockJms, new ServiceInvoker() {
			public void invoke( Message message ) throws Exception {
				throw new Exception( "TESTING" );
			}
		} );
		mockJms.sendToErrorQueue( mockMessage );
		expectLastCall();
		replay( mockJms );
		mdb.onMessage( mockMessage );
		verify( mockJms );
	}

}
