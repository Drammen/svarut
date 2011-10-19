package no.kommune.bergen.soa.common.jms;

public interface TextMessageListenerService {

	void service( String msg ) throws Exception;

}
