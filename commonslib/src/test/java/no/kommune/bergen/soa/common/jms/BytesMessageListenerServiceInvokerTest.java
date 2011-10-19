package no.kommune.bergen.soa.common.jms;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.junit.Assert.assertTrue;

import javax.jms.BytesMessage;

import org.junit.Test;

public class BytesMessageListenerServiceInvokerTest {

	@Test
	public void invoke() throws Exception {
		final boolean[] results = new boolean[1];
		final byte[] messageSent = "TESTING".getBytes();
		BytesMessage mockMessage = createStrictMock( BytesMessage.class );
		BytesMessageListenerServiceInvoker testObj = new BytesMessageListenerServiceInvoker( new BytesMessageListenerService() {
			public void service( byte[] msg ) throws Exception {
				results[0] = true;
			}
		} );
		expect( mockMessage.readBytes( isA( byte[].class ) ) ).andReturn( messageSent.length );
		expect( mockMessage.readBytes( isA( byte[].class ) ) ).andReturn( -1 );
		replay( mockMessage );
		testObj.invoke( mockMessage );
		verify( mockMessage );
		assertTrue( results[0] );
	}
}
