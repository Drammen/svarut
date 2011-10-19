package no.kommune.bergen.soa.common.jms;

import javax.jms.Message;
import javax.jms.TextMessage;

public class TextMessageListenerServiceInvoker implements ServiceInvoker {
	private final TextMessageListenerService textMessageListenerService;

	public TextMessageListenerServiceInvoker( TextMessageListenerService textMessageListenerService ) {
		this.textMessageListenerService = textMessageListenerService;
	}

	public void invoke( Message message ) throws Exception {
		String msg = null;
		try {
			TextMessage textMessage = (TextMessage) message;
			msg = textMessage.getText();
		} catch (Exception e) {
			throw new PoisonMessageException( "TextMessage content not available.", e );
		}
		textMessageListenerService.service( msg );
	}

}
