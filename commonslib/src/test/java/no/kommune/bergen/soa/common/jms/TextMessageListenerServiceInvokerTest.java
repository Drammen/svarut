package no.kommune.bergen.soa.common.jms;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.junit.Assert.assertEquals;

import javax.jms.TextMessage;

import org.junit.Test;

public class TextMessageListenerServiceInvokerTest {

	@Test
	public void invoke() throws Exception {
		final String messageSent = "TESTING";
		TextMessage mockMessage = createStrictMock( TextMessage.class );
		TextMessageListenerServiceInvoker testObj = new TextMessageListenerServiceInvoker( new TextMessageListenerService() {
			public void service( String msg ) throws Exception {
				assertEquals( messageSent, msg );
			}
		} );
		expect( mockMessage.getText() ).andReturn( messageSent );
		replay( mockMessage );
		testObj.invoke( mockMessage );
		verify( mockMessage );
	}

}
