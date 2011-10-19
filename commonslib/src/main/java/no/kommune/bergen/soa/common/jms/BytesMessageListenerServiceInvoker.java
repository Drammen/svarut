package no.kommune.bergen.soa.common.jms;

import java.io.ByteArrayOutputStream;

import javax.jms.BytesMessage;
import javax.jms.Message;

public class BytesMessageListenerServiceInvoker implements ServiceInvoker {
	private final BytesMessageListenerService byteMessageListenerService;

	public BytesMessageListenerServiceInvoker( BytesMessageListenerService byteMessageListenerService ) {
		this.byteMessageListenerService = byteMessageListenerService;
	}

	public void invoke( Message message ) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream( 1024 * 4 );
			BytesMessage bytesMessage = (BytesMessage) message;
			byte[] buffer = new byte[1024 * 4];
			int len = 0;
			while ((len = bytesMessage.readBytes( buffer )) != -1) {
				byteArrayOutputStream.write( buffer, 0, len );
			}
		} catch (Exception e) {
			throw new PoisonMessageException( "BytesMessage content not available.", e );
		} finally {
			if (byteArrayOutputStream != null) byteArrayOutputStream.close();
		}
		byteMessageListenerService.service( byteArrayOutputStream.toByteArray() );
	}

}
