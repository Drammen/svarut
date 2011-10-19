package no.kommune.bergen.soa.common.jms;

public interface BytesMessageListenerService {

	void service( byte[] msg ) throws Exception;

}
